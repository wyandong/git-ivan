package main.com.ivan.design_pattern.decorator;


public class DogCommonSound implements SoundAble {

    @Override
    public String makeSound() {
        return "汪汪汪";
    }
    
}
