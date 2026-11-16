package main.com.ivan.thread;

import java.util.ArrayDeque;
import java.util.Queue;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 生产者-消费者演示第 3 版：优雅关闭（graceful shutdown）。
 *
 * <p>与第 2 版（ProducerConsumerDemo2）的区别：
 * <ol>
 *   <li><b>不再用 InterruptedException 当业务信号</b>：
 *       put 返回 boolean（false = 队列已关闭），take 返回 null（null = 没有任务且已关闭），
 *       调用方通过返回值判断是否退出，真实中断和业务信号彻底分开；</li>
 *   <li><b>关闭后消费完存量</b>：shutdown() 之后生产者不再放入新任务，
 *       但消费者会把队列里剩余的任务全部处理完才退出，不丢数据；</li>
 *   <li><b>用计数器验证不丢不重</b>：结束时打印“生产数 == 消费数”。</li>
 * </ol>
 */
public class ProducerConsumerDemo3 {

    /**
     * 支持优雅关闭的阻塞队列。
     *
     * <p>关闭语义（这是“优雅关闭”的核心）：
     * <ul>
     *   <li>生产者侧：closed = true 后立即拒绝新任务，正在等待的也立刻返回 false；</li>
     *   <li>消费者侧：优先消费！只要队列里还有数据就继续 take，
     *       只有“队列空了 && 已关闭”才收工返回 null。</li>
     * </ul>
     *
     * @param <E> 元素类型
     */
    static class MyBlockingQueue<E> {

        /** 底层 FIFO 容器。final：构造后不再替换引用 */
        private final Queue<E> queue = new ArrayDeque<>();

        /** 容量上限 */
        private final int limit;

        /**
         * 关闭标记。
         * volatile：保证主线程 shutdown() 写入的 true，对生产者/消费者线程立即可见。
         * 虽然所有读写都在 synchronized 方法内（锁已保证可见性），
         * 这里写 volatile 是为了让语义更明确。
         */
        private volatile boolean closed = false;

        public MyBlockingQueue(int limit) {
            if (limit <= 0) {
                throw new IllegalArgumentException("limit 必须为正数: " + limit);
            }
            this.limit = limit;
        }

        /**
         * 生产者：放入元素。
         *
         * <p>返回值约定（用返回值代替抛异常表达“队列已关闭”）：
         * <ul>
         *   <li>true  —— 放入成功；</li>
         *   <li>false —— 队列已关闭，生产者应当收工退出。</li>
         * </ul>
         *
         * @return 是否放入成功
         */
        public synchronized boolean put(E e) throws InterruptedException {
            // 没关闭且队列满 → 等待消费者腾位置。
            // 必须 while 而不是 if：唤醒后条件可能又不满足（虚假唤醒/被别的线程抢先）
            while (!closed && queue.size() == limit) {
                wait();
            }
            // 走到这里只有两种情况：① 队列有空位了 ② closed 变成了 true
            if (closed) {
                return false;   // 停止接收新任务，让生产者正常退出
            }
            queue.add(e);
            // 状态变化：可能唤醒正在等“队列不为空”的消费者
            notifyAll();
            return true;
        }

        /**
         * 消费者：取出元素。
         *
         * <p>返回值约定：
         * <ul>
         *   <li>非 null —— 取到的任务；</li>
         *   <li>null   —— 队列已关闭且没有剩余任务，消费者应当收工退出。</li>
         * </ul>
         *
         * <p>注意与 put 的对称差异：put 只看 closed，take 是“先消费再检查关闭”，
         * 这正是“关闭后处理完存量、不丢任务”的关键。
         */
        public synchronized E take() throws InterruptedException {
            // 只要队列里还有数据，不管是否已关闭，都继续消费（处理完存量）
            while (queue.isEmpty()) {
                if (closed) {
                    // 没数据了，而且已经关闭：不会再有新任务，正常收工
                    return null;
                }
                // 空且未关闭 → 等待生产者放入（wait 会释放锁）
                wait();
            }
            E e = queue.poll();
            // 拿走一个，可能唤醒正在等“队列不满”的生产者
            notifyAll();
            return e;
        }

        /**
         * 优雅关闭：
         * 1. 置位 closed，之后 put 一律返回 false（拒绝新任务）；
         * 2. notifyAll() 唤醒所有正在 wait 的线程，让它们去检查新状态——
         *    如果不唤醒，卡在 wait 里的线程永远醒不来，程序就停不下来。
         */
        public synchronized void shutdown() {
            closed = true;
            notifyAll();
        }

        /** 当前队列中积压的任务数（用于演示打印） */
        public synchronized int size() {
            return queue.size();
        }
    }

    /**
     * 生产者线程：每 10ms 生产一个任务（快），目的是制造积压，
     * 让演示能看到“关闭时队列里还有任务，但最终全部被消费掉”。
     */
    static class Producer extends Thread {

        private final MyBlockingQueue<String> queue;
        /** 共享计数器：AtomicInteger 保证多线程累加的原子性与可见性 */
        private final AtomicInteger produced;

        // 构造函数：初始化队列、共享计数器、线程名
        Producer(MyBlockingQueue<String> queue, AtomicInteger produced) {
            this.queue = queue;
            this.produced = produced;
            setName("producer");    // 给线程命名，日志排查更清晰
        }

        @Override
        public void run() {
            int num = 0;
            try {
                while (true) {
                    String task = "task-" + num;
                    // put 返回 false 说明队列已关闭：正常退出循环，不再抛异常
                    if (!queue.put(task)) {
                        break;
                    }
                    produced.incrementAndGet();
                    System.out.println("[" + getName() + "] produce " + task);
                    num++;
                    // 生产快：只睡 10ms，消费者慢，队列容易满、容易积压
                    Thread.sleep(10);
                }
            } catch (InterruptedException e) {
                // 这里只剩“被 interrupt 打断”这一种来源，语义干净了。
                // 恢复中断标志，不吞掉中断状态（规范做法）
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + getName() + "] 生产者收工");
        }
    }

    /**
     * 消费者线程：每 150ms 消费一个任务（慢），处理速度跟不上生产速度，
     * 关闭时必然有积压，用来验证“存量会被处理完”。
     */
    static class Consumer extends Thread {

        private final MyBlockingQueue<String> queue;
        private final AtomicInteger consumed;

        Consumer(MyBlockingQueue<String> queue, AtomicInteger consumed, String name) {
            this.queue = queue;
            this.consumed = consumed;
            setName(name);
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // take 返回 null 说明“没任务了且已关闭”：正常收工
                    String task = queue.take();
                    if (task == null) {
                        break;
                    }
                    // 模拟业务处理耗时（消费者比生产者慢）
                    Thread.sleep(300);
                    consumed.incrementAndGet();
                    System.out.println("[" + getName() + "] handle  " + task);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            System.out.println("[" + getName() + "] 消费者收工");
        }
    }

    /**
     * 程序入口：主线程负责启动、关闭、收尾。
     */
    public static void main(String[] args) throws InterruptedException {

        MyBlockingQueue<String> queue = new MyBlockingQueue<>(3);

        // 共享计数器：两个消费者都会累加 consumed，必须用线程安全的 AtomicInteger
        AtomicInteger produced = new AtomicInteger(0);
        AtomicInteger consumed = new AtomicInteger(0);

        Producer producer = new Producer(queue, produced);
        Consumer consumer1 = new Consumer(queue, consumed, "consumer-1");
        Consumer consumer2 = new Consumer(queue, consumed, "consumer-2");

        // start() 创建新线程执行 run()，立即返回
        producer.start();
        consumer1.start();
        consumer2.start();

        // 让程序跑 2 秒（生产快消费慢，关闭时队列里一定有积压任务）
        Thread.sleep(2000);

        System.out.println("==== shutdown，队列排队中 " + queue.size()
        + " 个，另有消费中（在途）任务未计入 ====");

        // 优雅关闭：拒绝新任务，但消费者会把存量处理完
        queue.shutdown();

        // 主线程等待三个线程全部真正结束（它们会自己消费完存量再退出）
        producer.join();
        consumer1.join();
        consumer2.join();

        System.out.println("==== 全部线程结束 ====");
        System.out.println("生产总数 = " + produced.get() + "，消费总数 = " + consumed.get());
        // 两个数字相等，证明关闭过程既没丢任务，也没有重复消费
    }
}
