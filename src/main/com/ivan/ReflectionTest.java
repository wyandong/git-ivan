package main.com.ivan;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class ReflectionTest {

    private String username = "zhang san";
    private String tel = "1230000000";

    public ReflectionTest() {

    }

    public ReflectionTest(String username, String tel) {
        this.username = username;
        this.tel = tel;
    }

    public void outputInfo() {
        System.out.println("username：" + username + "，tel：" + tel);
    }

    public static void main(String[] args) {
ReflectionTest rt = null;
        try {
            //  rt = ReflectionTest.class.getDeclaredConstructor(new Class[] { String.class, String.class })
            //         .newInstance("ivan", "13800000000");
            rt = ReflectionTest.class.getDeclaredConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException
                | NoSuchMethodException e) {
            e.printStackTrace();
        }

        rt.outputInfo();

        // Class<?> cls = Integer.class;

        // try {
        // Method method = cls.getDeclaredMethod("parseInt", new Class[] { String.class
        // });
        // Object obj = method.invoke(null, "123");
        // System.out.println(obj);
        // } catch (NoSuchMethodException e) {
        // e.printStackTrace();
        // } catch (IllegalAccessException e) {
        // e.printStackTrace();
        // } catch (InvocationTargetException e) {
        // e.printStackTrace();
        // }
    }
}
