package main.com.ivan.design_pattern.decorator;


public class SoundAbleDecoratorDelay extends SoundAbleDecoratorBase{

    private int delayTime;
    public SoundAbleDecoratorDelay(SoundAble decorated, int delayTime) {
        super(decorated);
        this.delayTime = delayTime;
    }

    @Override
    public String makeSound() {
        try {
            Thread.sleep(delayTime);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        return "【延迟】" + delayTime/1000 + "【秒】"+ decorated.makeSound();
    }
    
}
