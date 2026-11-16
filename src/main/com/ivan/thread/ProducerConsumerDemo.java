package main.com.ivan.thread;

import java.util.ArrayDeque;
import java.util.Queue;

public class ProducerConsumerDemo {
    static class MyBlockingQueue<E> {
        private Queue<E> queue = null;
        private int limit;

        public MyBlockingQueue(int limit) {
            this.limit = limit;
            queue = new ArrayDeque<>(limit);
        }

        public synchronized void put(E e) throws InterruptedException {
            while (queue.size() == limit) {
                wait();
            }
            queue.add(e);
            notifyAll();
        }

        public synchronized E take() throws InterruptedException {
            while (queue.isEmpty()) {
                wait();
            }
            E e = queue.poll();
            notifyAll();
            return e;
        }
    }

    // 共享停止标记，volatile保证多线程可见性
    static volatile boolean isRunning = true;

    static class Producer extends Thread {
        MyBlockingQueue<String> queue;
        public Producer(MyBlockingQueue<String> queue) {
            this.queue = queue;
        }
        @Override
        public void run() {
            int num = 0;
            try {
                // 不再是while(true)，判断开关
                while (isRunning) {
                    String task = String.valueOf(num);
                    queue.put(task);
                    System.out.println("produce task " + task);
                    num++;
                    Thread.sleep((int) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                System.out.println("生产者收到中断，退出");
            }
        }
    }

    static class Consumer extends Thread {
        MyBlockingQueue<String> queue;
        public Consumer(MyBlockingQueue<String> queue) {
            this.queue = queue;
        }
        @Override
        public void run() {
            try {
                while (isRunning) {
                    String task = queue.take();
                    System.out.println("handle task " + task);
                    Thread.sleep((int) (Math.random() * 100));
                }
            } catch (InterruptedException e) {
                System.out.println("消费者收到中断，退出");
            }
        }
    }


    public static void main(String[] args) throws InterruptedException {
        MyBlockingQueue<String> queue = new MyBlockingQueue<>(3);
        Producer producer = new Producer(queue);
        Consumer consumer1 = new Consumer(queue);
        Consumer consumer2 = new Consumer(queue);

        producer.start();
        consumer1.start();
        consumer2.start();

        // 模拟：主线程5秒之后，通知全部线程停止
        Thread.sleep(5000);
        System.out.println("====准备停止所有线程====");
        isRunning = false;

        // 唤醒所有卡在wait的线程，不然它们会一直睡着，感知不到isRunning变化
        synchronized (queue){
            queue.notifyAll();
        }

        // 等待子线程结束，主线程最后退出
        producer.join();
        consumer1.join();
        consumer2.join();
        System.out.println("全部线程正常结束，程序退出");
    }
}
