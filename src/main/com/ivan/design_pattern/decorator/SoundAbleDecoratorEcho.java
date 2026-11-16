package main.com.ivan.design_pattern.decorator;


public class SoundAbleDecoratorEcho extends SoundAbleDecoratorBase {

    public SoundAbleDecoratorEcho(SoundAble decorated) {
        super(decorated);
    }

    @Override
    public String makeSound() {
        return decorated.makeSound() + "【回声】";
    }
    
}
