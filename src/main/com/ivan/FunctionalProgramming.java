package main.com.ivan;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;

/**
 * Java 函数式编程示例
 * 
 * 函数式编程是一种编程范式，核心思想：
 * 1. 函数是一等公民（可以作为参数传递、返回值）
 * 2. 使用 Lambda 表达式和 Stream API
 * 3. 强调不可变性和无副作用
 */
public class FunctionalProgramming {

    // ==================== 1. 函数式接口 ====================
    
    /**
     * @FunctionalInterface 表示该接口只有一个抽象方法
     * Java 8 自带了许多函数式接口，位于 java.util.function 包下
     */
    @FunctionalInterface
    interface StringTransformer {
        String transform(String input);
        // 可以有 default 和 static 方法
        default String twice(String input) {
            return transform(transform(input));
        }
    }

    // ==================== 2. Lambda 表达式 ====================

    public static void lambdaExamples() {
        System.out.println("=== Lambda 表达式 ===");

        // 基本语法：(参数列表) -> { 方法体 }
        StringTransformer toUpper = s -> s.toUpperCase();
        System.out.println(toUpper.transform("hello"));       // HELLO
        System.out.println(toUpper.twice("hello"));           // HELLO

        // 使用 Java 内置的函数式接口
        Predicate<String> isEmpty = s -> s.isEmpty();
        System.out.println("空字符串判断: " + isEmpty.test(""));  // true

        Function<Integer, String> intToString = n -> "数字: " + n;
        System.out.println(intToString.apply(42));                 // 数字: 42

        Consumer<String> printer = s -> System.out.println("消费: " + s);
        printer.accept("数据");                                    // 消费: 数据

        Supplier<Double> random = () -> Math.random();
        System.out.println("随机数: " + random.get());             // 随机数

        // BiFunction 接收两个参数
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        System.out.println("2 + 3 = " + add.apply(2, 3));         // 5
    }

    // ==================== 3. 方法引用 ====================

    public static void methodReferenceExamples() {
        System.out.println("\n=== 方法引用 ===");

        List<String> names = Arrays.asList("Alice", "Bob", "Charlie");

        // 静态方法引用  ClassName::staticMethod
        names.forEach(System.out::println);

        // 实例方法引用  instance::method
        StringTransformer toLower = String::toLowerCase;
        System.out.println(toLower.transform("HELLO"));   // hello

        // 构造器引用  ClassName::new
        Supplier<StringBuilder> sbSupplier = StringBuilder::new;
        StringBuilder sb = sbSupplier.get();
        sb.append("构造器引用");
        System.out.println(sb);
    }

    // ==================== 4. Stream API ====================

    public static void streamExamples() {
        System.out.println("\n=== Stream API ===");

        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);

        // 中间操作（返回新 Stream）：filter、map、sorted、distinct、limit
        // 终端操作（触发计算）：collect、forEach、reduce、count、findFirst

        // 示例1：过滤偶数，乘以2，收集为列表
        List<Integer> result = numbers.stream()
                .filter(n -> n % 2 == 0)      // 过滤偶数
                .map(n -> n * 2)              // 每个数乘以2
                .collect(Collectors.toList()); // 收集为 List
        System.out.println("偶数×2: " + result);    // [4, 8, 12, 16, 20]

        // 示例2：求和
        int sum = numbers.stream()
                .reduce(0, Integer::sum);
        System.out.println("总和: " + sum);          // 55

        // 示例3：分组
        List<String> words = Arrays.asList("cat", "dog", "elephant", "ant", "bee");
        Map<Integer, List<String>> groupedByLength = words.stream()
                .collect(Collectors.groupingBy(String::length));
        System.out.println("按长度分组: " + groupedByLength);

        // 示例4：扁平映射
        List<List<Integer>> matrix = Arrays.asList(
                Arrays.asList(1, 2), Arrays.asList(3, 4), Arrays.asList(5, 6)
        );
        List<Integer> flattened = matrix.stream()
                .flatMap(List::stream)
                .collect(Collectors.toList());
        System.out.println("扁平化: " + flattened);  // [1, 2, 3, 4, 5, 6]
    }

    // ==================== 5. Optional ====================

    public static void optionalExample() {
        System.out.println("\n=== Optional ===");

        String name = null;
        Optional<String> opt = Optional.ofNullable(name);

        // 安全取值
        System.out.println("默认值: " + opt.orElse("匿名用户"));  // 匿名用户

        // 链式操作
        Optional.ofNullable("Hello World")
                .map(String::toUpperCase)
                .filter(s -> s.length() > 5)
                .ifPresent(System.out::println);   // HELLO WORLD
    }

    // ==================== 6. 函数组合 ====================

    public static void functionComposition() {
        System.out.println("\n=== 函数组合 ===");

        // andThen：先执行当前函数，再执行传入函数
        Function<Integer, Integer> doubleIt = n -> n * 2;
        Function<Integer, Integer> addTen = n -> n + 10;

        Function<Integer, Integer> composed = doubleIt.andThen(addTen);
        System.out.println("(5 × 2) + 10 = " + composed.apply(5));   // 20

        // compose：先执行传入函数，再执行当前函数
        Function<Integer, Integer> composed2 = doubleIt.compose(addTen);
        System.out.println("(5 + 10) × 2 = " + composed2.apply(5));   // 30
    }

    public static void main(String[] args) {
        lambdaExamples();
        methodReferenceExamples();
        streamExamples();
        optionalExample();
        functionComposition();
    }
}