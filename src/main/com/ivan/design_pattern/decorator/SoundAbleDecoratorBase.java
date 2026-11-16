package main.com.ivan.design_pattern.decorator;


public abstract class SoundAbleDecoratorBase implements SoundAble {

    // 被装饰的对象
    protected SoundAble decorated;

    // 构造方法，传入被装饰的对象
    public SoundAbleDecoratorBase(SoundAble decorated)
    {
        this.decorated = decorated;
    }
}
