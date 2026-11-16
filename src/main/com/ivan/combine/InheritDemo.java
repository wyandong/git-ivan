package main.com.ivan.combine;

// 汪汪叫的行为
class BarkSound implements Soundable {
    @Override
    public void makeSound() {
        System.out.println("汪汪汪！");
    }
}

// 喵喵叫的行为
class MeowSound implements Soundable {
    @Override
    public void makeSound() {
        System.out.println("喵喵喵~");
    }
}

// 静音（不会叫）的行为
class SilentSound implements Soundable {
    @Override
    public void makeSound() {
        // 什么都不做
    }
}

// 奔跑移动
class RunMove implements Movable {
    @Override
    public void move() {
        System.out.println("用四条腿奔跑");
    }
}

// 爬行移动
class CrawlMove implements Movable {
    @Override
    public void move() {
        System.out.println("爬行前进");
    }
}

class Animal {
    // 组合：拥有这些能力（接口类型）
    protected Soundable soundable;
    protected Movable movable;

    // 通过构造器注入行为
    public Animal(Soundable soundable, Movable movable) {
        this.soundable = soundable;
        this.movable = movable;
    }

    // 委托给组合的组件执行
    public void makeSound() {
        soundable.makeSound();
    }

    public void move() {
        movable.move();
    }
}

public class InheritDemo {
    public static void main(String[] args) {
        // 普通狗：汪汪叫 + 奔跑
        Animal normalDog = new Animal(new BarkSound(), new RunMove());
        System.out.println("--- 普通狗 ---");
        normalDog.makeSound(); // 汪汪汪！
        normalDog.move();      // 用四条腿奔跑

        // 静音狗：不会叫 + 奔跑（继承做不到这一点！）
        Animal silentDog = new Animal(new SilentSound(), new RunMove());
        System.out.println("\n--- 静音狗 ---");
        silentDog.makeSound(); // （静默）
        silentDog.move();      // 用四条腿奔跑

        // 猫：喵喵叫 + 奔跑
        Animal cat = new Animal(new MeowSound(), new RunMove());
        System.out.println("\n--- 猫 ---");
        cat.makeSound();       // 喵喵喵~
        cat.move();            // 用四条腿奔跑

        // 机器人狗：汪汪叫 + 爬行（机器不能继承 Animal！）
        Animal robotDog = new Animal(new BarkSound(), new CrawlMove());
        System.out.println("\n--- 机器人狗 ---");
        robotDog.makeSound();  // 汪汪汪！
        robotDog.move();       // 爬行前进
    }
}
