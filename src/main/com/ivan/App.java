package main.com.ivan;

import java.io.File;
import java.util.Arrays;
import java.util.Date;
import java.util.stream.Stream;

public class App {
    public static void main(String[] args) throws Exception {
        // FunctionalProgramming.main(args);
        System.out.println("access remote github repository");

        System.out.println(Date.class.getName());
        System.out.println(Date.class.getSimpleName());
        System.out.println(Date.class.getCanonicalName());
        System.out.println(Date.class.getPackage());
        try {
            Class<?> cls = Class.forName("java.util.HashMap");
            System.out.println(cls.getName());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println("==============");

        File[] files = new File("D:\\Personal").listFiles();
        Arrays.stream(files).filter(f -> f.isFile()).map(File::getName)
                .forEach(System.out::println);

        System.out.println("==============");

        Stream.generate(() -> Math.random()).limit(10).forEach(System.out::println);

        System.out.println("==============");

        Stream.iterate(1, n -> n + 2).limit(20).forEach(System.out::println);

    }
}