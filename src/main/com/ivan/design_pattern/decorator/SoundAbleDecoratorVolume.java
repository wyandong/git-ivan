package main.com.ivan.design_pattern.decorator;

public class SoundAbleDecoratorVolume extends SoundAbleDecoratorBase {

    private int volume;
    public SoundAbleDecoratorVolume(SoundAble decorated, int volume) {
        super(decorated);
        this.volume = volume;
    }
    
    @Override
    public String makeSound() {
        return "【音量】" + volume + "【dB】"+ decorated.makeSound();
    }
}
