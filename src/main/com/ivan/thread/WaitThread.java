package main.com.ivan.thread;

public class WaitThread extends Thread {

    private volatile boolean fire = false; // 标记：是否收到开火指令

    // 子线程run方法：等待指令
    @Override
    public void run() {
        System.out.println(Thread.currentThread().getName() + "  ->  executed wait");
        try {
            synchronized (this) { // 锁对象 = waitThread实例本身（this）
                while (!fire) { // ✅while循环判断条件（重点！不用if）
                    wait(); // ✅调用wait()
                }
            }
            System.out.println(Thread.currentThread().getName() + "  ->  executed fired");
        } catch (InterruptedException e) {
        }
    }

    // 主线程调用的fire()：发送指令、唤醒等待线程
    public synchronized void fire() {
        this.fire = true;
        notify(); // 唤醒在this对象上wait的线程
    }

    // main主线程
    public static void main(String[] args) throws InterruptedException {

        WaitThread waitThread = new WaitThread();

        System.out.println(Thread.currentThread().getName() + " ->  executed start");

        waitThread.start(); // 启动子线程

        Thread.sleep(5000); // ✅主线程休眠1秒（子线程有时间先进入wait）

        System.out.println(Thread.currentThread().getName() + " ->  executed set fire as true");
        waitThread.fire(); // 发送开火指令
    }
}