package main.com.ivan.thread;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

// 自定义异常：余额不足
class NoEnoughMoneyException extends Exception {
}

class Account {
    private double balance;
    public final Lock lock = new ReentrantLock();

    public Account(double balance) {
        this.balance = balance;
    }

    public double getMoney() {
        return balance;
    }

    public void reduce(double amount) {
        balance -= amount;
    }

    public void add(double amount) {
        balance += amount;
    }
}

public class TransferDemo {

    /**
     * 尝试转账：tryTransfer
     * 返回 true 代表两把锁都拿到、转账完成；
     * 只要有一把拿不到，就释放已经拿到的锁，返回 false
     */
    public static boolean tryTransfer(Account from, Account to, double money)
            throws NoEnoughMoneyException {

        // 第 1 步：尝试获取 from 的锁
        if (from.lock.tryLock()) {          // ← 拿到了才进 if 块
            try {

                // 第 2 步：再尝试获取 to 的锁
                if (to.lock.tryLock()) {    // ← 两把锁都拿到才进这里
                    try {
                        // 第 3 步：检查余额并执行转账
                        if (from.getMoney() >= money) {
                            from.reduce(money);
                            to.add(money);
                        } else {
                            throw new NoEnoughMoneyException();
                        }
                        return true;        // 转账成功，返回 true
                    } finally {
                        // 第 4 步：释放 to 的锁（外层 finally 会释放 from）
                        to.lock.unlock();
                    }
                }
                // 如果走到这里：from 拿到了，to 没拿到
                // → 直接跳出内层 if，外层 finally 负责释放 from 的锁

            } finally {
                // 第 5 步：释放 from 的锁
                from.lock.unlock();
            }
        }
        // 走到这里有两种可能：
        //   a) from.lock.tryLock() 一开始就返回 false（from 被别人占着）
        //   b) from 拿到了，但 to.lock.tryLock() 返回 false（to 被别人占着）
        return false;
    }

    // 尝试转账：tryTransfer2
    // 同时尝试拿两把锁，转账成功后释放两把锁，
    public static boolean tryTransfer2(Account from, Account to, double money)
        throws NoEnoughMoneyException {

    // 同时尝试拿两把锁
    if (from.lock.tryLock() && to.lock.tryLock()) {
        try {
            if (from.getMoney() >= money) {
                from.reduce(money);
                to.add(money);
            } else {
                throw new NoEnoughMoneyException();
            }
            return true;
        } finally {
            to.lock.unlock();
            from.lock.unlock();
        }
    }
    // 只要有一把没拿到，都不用释放（因为没持有）
    return false;
}

    /**
     * 对外的 transfer：循环重试，直到成功
     */
    public static void transfer(Account from, Account to, double money)
            throws NoEnoughMoneyException {
        boolean success;
        do {
            success = tryTransfer(from, to, money);

            // 如果抢锁失败，则循环重试
            if (!success) {
                // 抢锁失败，让出 CPU，给别的线程运行机会
                Thread.yield();
            }
        } while (!success);
    }

    public static void main(String[] args) throws Exception {
        Account a = new Account(1000);
        Account b = new Account(1000);

        // 线程 1：a 转给 b
        Thread t1 = new Thread(() -> {
            try {
                transfer(a, b, 10);
                System.out.println("t1 转账完成");
            } catch (NoEnoughMoneyException e) {
                e.printStackTrace();
            }
        });

        // 线程 2：b 转给 a（反向转账，容易触发死锁场景）
        Thread t2 = new Thread(() -> {
            try {
                transfer(b, a, 10);
                System.out.println("t2 转账完成");
            } catch (NoEnoughMoneyException e) {
                e.printStackTrace();
            }
        });

        t1.start();
        t2.start();
        t1.join();
        t2.join();

        System.out.println("a 余额：" + a.getMoney());
        System.out.println("b 余额：" + b.getMoney());
    }
}

