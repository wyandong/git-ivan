package main.com.ivan.thread;

import java.util.ArrayDeque;
import java.util.Queue;

/**
 * 生产者-消费者演示（wait / notifyAll 版本）。
 *
 * <p>整体结构：
 * <pre>
 *   MyBlockingQueue  —— 自己实现的“阻塞队列”，内部用 synchronized + wait/notifyAll 控制并发
 *   Producer         —— 生产者线程：不断往队列里 put
 *   Consumer         —— 消费者线程：不断从队列里 take
 *   main             —— 启动 1 个生产者 + 2 个消费者，跑 5 秒后统一停止
 * </pre>
 *
 * <p>核心思想：队列满时生产者“睡”，队列空时消费者“睡”，
 * 任何一方改变了队列状态就唤醒对方，避免 CPU 空转（忙等待）。
 */
public class ProducerConsumerDemo2 {

    /**
     * 自定义阻塞队列（简化版）。
     *
     * <p>为什么叫“阻塞”：当条件不满足时，调用线程会停在 wait() 上不往下走，
     * 既不消耗 CPU，也不释放锁以外的资源；等条件满足被唤醒后才继续。
     *
     * <p>三个关键角色：
     * <ul>
     *   <li>锁对象：就是 this（因为方法都加了 synchronized，锁的是当前 MyBlockingQueue 实例）</li>
     *   <li>条件：队列满（生产者等）、队列空（消费者等）</li>
     *   <li>通知：状态变化后 notifyAll() 唤醒所有等待者，让它们重新检查条件</li>
     * </ul>
     *
     * @param <E> 队列中元素的类型
     */
    static class MyBlockingQueue<E> {

        /**
         * 底层容器。ArrayDeque 是双端队列，这里只使用“尾部加、头部取”的 FIFO 用法。
         * 注意：ArrayDeque(limit) 里的 limit 只是“初始容量”，不是容量上限，
         * 真正的上限判断由下面的 limit 字段 + while 循环保证。
         */
        private Queue<E> queue;

        /** 容量上限。final：构造完成后不再改变，保证 put 里的判断始终基于同一个值 */
        private final int limit;

        /**
         * 停止标记。
         *
         * <p>volatile 保证“可见性”：一个线程把它改成 true 后，其他线程能立刻读到最新值，
         * 而不是读到自己工作内存里的旧缓存。
         *
         * <p>严格说这里读写都发生在 synchronized 代码块内，synchronized 本身已保证可见性，
         * volatile 属于“双保险 + 语义更清晰”。
         */
        private volatile boolean stop = false;

        /**
         * 构造队列。
         *
         * @param limit 允许同时存放的最大元素个数
         */
        public MyBlockingQueue(int limit) {
            this.limit = limit;
            queue = new ArrayDeque<>(limit);
        }

        /**
         * 对外提供“停止”能力：把标记置为 true，并唤醒所有正在 wait 的线程。
         *
         * <p>为什么要 synchronized：notifyAll() 必须在持有该对象锁时调用，
         * 否则抛 IllegalMonitorStateException。
         *
         * <p>为什么需要唤醒：此时生产者/消费者可能正卡在 wait() 里，
         * 不唤醒的话它们永远不会去检查 stop 标记，线程就退不出去。
         */
        public synchronized void stopQueue() {
            stop = true;
            notifyAll();
        }

        /**
         * 生产者：往队列里放数据。
         *
         * <p>synchronized 修饰实例方法，等价于整个方法体被
         * {@code synchronized (this) { ... }} 包住：同一时刻只允许一个线程进入，
         * 保证 size 判断与 add 之间不会被其他线程插队。
         *
         * @param e 要放入的元素
         * @throws InterruptedException 队列已停止（本示例把它当作“退出信号”抛出）
         */
        public synchronized void put(E e) throws InterruptedException {

            /*
             * 这里必须是 while 而不是 if：
             * 1. wait() 被唤醒后需要重新抢锁，抢到锁时条件可能已经被别的线程改回不满足了；
             * 2. 存在“虚假唤醒”（spurious wakeup），系统可能在没调用 notify 的情况下唤醒线程。
             * 所以唤醒后必须再判断一次，不满足就继续等。
             */
            while (!stop && queue.size() == limit) {
                // wait() 做三件事：释放锁 → 进入该对象的等待队列休眠 → 被唤醒后重新竞争锁
                // 因为释放了锁，消费者才有机会进来 take()，这就是它能工作的原因
                wait();
            }

            /*
             * 醒来后如果发现是“停止”导致的唤醒，就抛出异常。
             * 这里借用 InterruptedException 作为退出信号，好处是不用额外定义新异常；
             * 缺点是语义上不太准确（详见文末说明），生产代码更推荐自定义异常或返回 boolean。
             */
            if (stop) {
                throw new InterruptedException("队列已停止，禁止放入新任务");
            }

            // 条件满足（没停止且队列没满），真正入队
            queue.add(e);

            /*
             * 通知所有等待者。为什么用 notifyAll 而不是 notify：
             * 等待队列里可能既有生产者也有消费者，notify() 随机唤醒一个，
             * 如果唤醒的是另一个生产者，真正的消费者仍没醒，就可能一直卡住（信号丢失）。
             * notifyAll 唤醒后各自重新检查自己的 while 条件，不满足的继续睡，逻辑更安全。
             */
            notifyAll();
        }

        /**
         * 消费者：从队列里取数据。
         *
         * <p>与 put 完全对称：空队列时等待，取到后唤醒可能在等“队列不满”的生产者。
         *
         * @return 队首元素
         * @throws InterruptedException 队列已停止（本示例当作“退出信号”抛出）
         */
        public synchronized E take() throws InterruptedException {
            // 队列为空且未停止 → 等待生产者放入新元素
            while (!stop && queue.isEmpty()) {
                wait();
            }

            if (stop) {
                throw new InterruptedException("队列已停止，不再消费任务");
            }

            // poll() 取队首并删除；此处已在锁内确认非空，返回的不会是 null
            E e = queue.poll();

            // 拿走一个元素后队列空出了一个位置，唤醒可能正在等“不满”的生产者
            notifyAll();
            return e;
        }

        /*
         * 说明：这里的 stop 语义是“立刻停”，队列里尚未消费的数据会被直接丢弃。
         * 如果业务要求“停止后把剩余任务处理完再退出”，需要改成：
         *   put 检查 stop 直接拒绝；take 只在 stop && isEmpty 时才退出。
         */
    }

    /**
     * 生产者线程。
     *
     * <p>继承 Thread 并重写 run()：run() 里的代码就是新线程启动后要执行的内容。
     * 注意必须调用 start() 才会真正开线程，直接调用 run() 只是在当前线程同步执行。
     */
    static class Producer extends Thread {

        /** 多个线程共享同一个队列实例，它是线程之间通信的“桥梁” */
        MyBlockingQueue<String> queue;

        public Producer(MyBlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            int num = 0;
            try {
                while (true) {                       // 生产者是“无限循环”的，靠异常跳出
                    String task = String.valueOf(num);
                    // 队列满时这里会阻塞等待，直到消费者腾出位置
                    queue.put(task);
                    System.out.println("produce task " + task);
                    num++;
                    // 随机休眠 0~99ms，模拟生产任务需要耗时，也避免刷屏太快
                    Thread.sleep((int) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                /*
                 * 两种 InterruptedException 都会到这里：
                 * 1. queue.put() 因为 stop 标记抛出的“停止信号”；
                 * 2. Thread.sleep() 被真正 interrupt() 打断。
                 * 两种情况都意味着“不该再干活了”，直接结束 run()，线程自然终止。
                 */
                System.out.println("【生产者】收到停止信号，线程退出");
            }
        }
    }

    /**
     * 消费者线程。与生产者结构一样，只是循环体换成了 take()。
     * 这里会启动两个消费者，用来观察“多个消费者竞争同一个队列”的效果。
     */
    static class Consumer extends Thread {

        MyBlockingQueue<String> queue;

        public Consumer(MyBlockingQueue<String> queue) {
            this.queue = queue;
        }

        @Override
        public void run() {
            try {
                while (true) {
                    // 队列空时阻塞等待；被唤醒并取到元素后返回
                    String task = queue.take();
                    System.out.println("handle task " + task);
                    // 模拟处理任务耗时
                    Thread.sleep((int) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                System.out.println("【消费者】收到停止信号，线程退出");
            }
        }
    }

    /**
     * 程序入口：主线程负责“启动”和“收尾”。
     */
    public static void main(String[] args) throws InterruptedException {

        // 容量为 3 的队列：最多缓存 3 个未被消费的任务，超过就让生产者等待（起到削峰/背压作用）
        MyBlockingQueue<String> queue = new MyBlockingQueue<>(3);

        Producer producer = new Producer(queue);
        Consumer consumer1 = new Consumer(queue);
        Consumer consumer2 = new Consumer(queue);

        // start() 会创建一个新的操作系统线程，并在其中执行 run()，立即返回，不阻塞主线程
        producer.start();
        consumer1.start();
        consumer2.start();

        // 主线程自己睡 5 秒，让生产者/消费者跑一会儿
        Thread.sleep(5000);
        System.out.println("====准备停止所有线程====");

        // 修改停止标记 + 唤醒所有等待线程
        // 关键点：释放锁、唤醒必须由队列内部完成，外部不能直接操作 wait/notify
        queue.stopQueue();

        /*
         * join()：主线程阻塞等待 producer 线程执行结束。
         * 如果没有 join，main 方法会直接打印最后一行并结束，
         * 而 JVM 只有等所有非守护线程结束后才退出，输出顺序会很乱。
         */
        producer.join();
        consumer1.join();
        consumer2.join();

        System.out.println("全部线程正常结束，程序退出");
    }
}
