package main.com.ivan;

public class Child extends Base{
    public void print(){
        System.out.println("Child class");
    }

    public static void main(String[] args) {

        //
        Base a = new Base();

        //
        Base b = new Child();
        
        System.out.println(a instanceof Child);
        System.out.println(b instanceof Child);
    }
}