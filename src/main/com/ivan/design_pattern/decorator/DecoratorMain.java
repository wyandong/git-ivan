package main.com.ivan.design_pattern.decorator;

public class DecoratorMain {
    public static void main(String[] args) {
        SoundAble superDog = new SoundAbleDecoratorDelay(
                new SoundAbleDecoratorVolume(new SoundAbleDecoratorEcho(new DogCommonSound()), 5), 6000);

        System.out.println(superDog.makeSound());
    }
}
