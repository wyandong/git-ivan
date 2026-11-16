package main.com.ivan;

import java.util.*;
import java.util.function.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Java 预定义函数式接口详解
 * 
 * Java 8 在 java.util.function 包中提供了丰富的预定义函数式接口：
 * 
 * ┌─────────────────┬────────────────────────┬──────────────────────────┐
 * │ 接口名          │ 方法签名                │ 用途                      │
 * ├─────────────────┼────────────────────────┼──────────────────────────┤
 * │ Predicate<T>     │ boolean test(T t)       │ 断言/条件判断              │
 * │ Function<T,R>    │ R apply(T t)            │ 转换/映射                  │
 * │ Consumer<T>      │ void accept(T t)        │ 消费/处理数据              │
 * │ Supplier<T>      │ T get()                 │ 生产/提供数据              │
 * │ BiFunction<T,U,R>│ R apply(T t, U u)       │ 双参数转换                 │
 * │ BiConsumer<T,U>  │ void accept(T t, U u)   │ 双参数消费                 │
 * │ UnaryOperator<T> │ T apply(T t)            │ 一元操作(Function子类)     │
 * │ BinaryOperator<T>│ T apply(T t1, T t2)     │ 二元操作(BiFunction子类)   │
 * └─────────────────┴────────────────────────┴──────────────────────────┘
 */
public class PredefinedFunctionalInterfaces {

    public static void main(String[] args) {
        System.out.println("========== Java 预定义函数式接口演示 ==========\n");
        
        predicateExamples();
        functionExamples();
        consumerExamples();
        supplierExamples();
        biFunctionExamples();
        unaryOperatorExamples();
        binaryOperatorExamples();
        compositionExamples();
        streamWithFunctionalInterfaces();
        practicalApplications();
    }

    // ==================== 1. Predicate<T> - 断言接口 ====================
    /**
     * Predicate 接收一个参数，返回 boolean
     * 常用于过滤、条件判断
     * 
     * 典型应用场景：
     * - 集合过滤
     * - 条件校验
     * - 数据筛选
     */
    static void predicateExamples() {
        System.out.println("===== 1. Predicate<T> 断言接口 =====");
        
        // 基础用法：判断数字是否为偶数
        Predicate<Integer> isEven = n -> n % 2 == 0;
        System.out.println("4 是偶数? " + isEven.test(4));   // true
        System.out.println("7 是偶数? " + isEven.test(7));   // false
        
        // 字符串判断
        Predicate<String> isEmpty = s -> s.isEmpty();
        Predicate<String> isLong = s -> s.length() > 5;
        System.out.println("空字符串? " + isEmpty.test(""));      // true
        System.out.println("长字符串? " + isLong.test("Hello"));  // false
        
        // --- 组合 Predicate ---
        // and: 两个条件都满足
        Predicate<Integer> isPositiveAndEven = n -> n > 0 && n % 2 == 0;
        System.out.println("6 是正偶数? " + isPositiveAndEven.test(6));  // true
        
        // or: 满足任一条件
        Predicate<Integer> isNegativeOrOdd = n -> n < 0 || n % 2 != 0;
        System.out.println("-3 是负数或奇数? " + isNegativeOrOdd.test(-3));  // true
        
        // negate: 取反
        Predicate<Integer> isNotEven = isEven.negate();
        System.out.println("5 不是偶数? " + isNotEven.test(5));  // true
        
        // 典型应用：集合过滤
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        List<Integer> evenNumbers = filterList(numbers, isEven);
        System.out.println("过滤偶数: " + evenNumbers);  // [2, 4, 6, 8, 10]
        
        List<Integer> greaterThan5 = filterList(numbers, n -> n > 5);
        System.out.println("大于5: " + greaterThan5);    // [6, 7, 8, 9, 10]
        
        System.out.println();
    }
    
    // 使用 Predicate 进行通用过滤
    static <T> List<T> filterList(List<T> list, Predicate<T> predicate) {
        List<T> result = new ArrayList<>();
        for (T item : list) {
            if (predicate.test(item)) {
                result.add(item);
            }
        }
        return result;
    }

    // ==================== 2. Function<T, R> - 转换接口 ====================
    /**
     * Function 接收 T 类型参数，返回 R 类型结果
     * 用于数据转换、映射
     * 
     * 典型应用场景：
     * - 数据类型转换
     * - 对象属性提取
     * - Stream.map() 操作
     */
    static void functionExamples() {
        System.out.println("===== 2. Function<T, R> 转换接口 =====");
        
        // 字符串转整数
        Function<String, Integer> strLength = s -> s.length();
        System.out.println("'Hello' 长度: " + strLength.apply("Hello"));  // 5
        
        // 整数转字符串
        Function<Integer, String> intToStr = n -> "数字: " + n;
        System.out.println(intToStr.apply(42));  // 数字: 42
        
        // 对象转换：字符串转大写
        Function<String, String> toUpper = s -> s.toUpperCase();
        System.out.println("转大写: " + toUpper.apply("hello world"));  // HELLO WORLD
        
        // --- 组合 Function ---
        Function<Integer, Integer> doubleIt = n -> n * 2;
        Function<Integer, Integer> addTen = n -> n + 10;
        
        // andThen: 先执行当前函数，再执行传入函数
        Function<Integer, Integer> doubleThenAdd = doubleIt.andThen(addTen);
        System.out.println("(5 × 2) + 10 = " + doubleThenAdd.apply(5));  // 20
        
        // compose: 先执行传入函数，再执行当前函数
        Function<Integer, Integer> addThenDouble = doubleIt.compose(addTen);
        System.out.println("(5 + 10) × 2 = " + addThenDouble.apply(5));  // 30
        
        // identity: 返回输入本身
        Function<String, String> identity = Function.identity();
        System.out.println("identity: " + identity.apply("保持不变"));  // 保持不变
        
        // 典型应用：转换列表
        List<String> names = Arrays.asList("alice", "bob", "charlie");
        List<String> upperNames = mapList(names, String::toUpperCase);
        System.out.println("姓名转大写: " + upperNames);  // [ALICE, BOB, CHARLIE]
        
        List<Integer> nameLengths = mapList(names, String::length);
        System.out.println("姓名长度: " + nameLengths);  // [5, 3, 7]
        
        System.out.println();
    }
    
    // 使用 Function 进行通用映射
    static <T, R> List<R> mapList(List<T> list, Function<T, R> mapper) {
        List<R> result = new ArrayList<>();
        for (T item : list) {
            result.add(mapper.apply(item));
        }
        return result;
    }

    // ==================== 3. Consumer<T> - 消费接口 ====================
    /**
     * Consumer 接收一个参数，执行操作但不返回值
     * 用于"消费"数据，如打印、存储、发送等
     * 
     * 典型应用场景：
     * - 打印输出
     * - 集合遍历处理
     * - 日志记录
     * - Stream.forEach()
     */
    static void consumerExamples() {
        System.out.println("===== 3. Consumer<T> 消费接口 =====");
        
        // 打印消费者
        Consumer<String> printer = s -> System.out.println("  消费: " + s);
        printer.accept("Hello");   // 消费: Hello
        printer.accept("World");   // 消费: World
        
        // 累加器（虽然内部修改状态，但主要目的是消费）
        StringBuilder sb = new StringBuilder();
        Consumer<String> appender = s -> sb.append(s).append(" ");
        appender.accept("Java");
        appender.accept("Python");
        appender.accept("Go");
        System.out.println("  累积结果: " + sb.toString());  // Java Python Go
        
        // --- 组合 Consumer ---
        Consumer<String> logConsumer = s -> System.out.println("  [LOG] " + s);
        Consumer<String> upperConsumer = s -> System.out.println("  [UPPER] " + s.toUpperCase());
        
        // andThen: 顺序执行
        Consumer<String> logThenUpper = logConsumer.andThen(upperConsumer);
        logThenUpper.accept("test");
        // 输出:
        //   [LOG] test
        //   [UPPER] TEST
        
        // 典型应用：遍历集合
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        System.out.print("  遍历: ");
        numbers.forEach(n -> System.out.print(n + " "));  // 1 2 3 4 5
        System.out.println();
        
        // 双消费者 BiConsumer
        BiConsumer<String, Integer> wordLengthPrinter = (word, len) -> 
            System.out.println("  '" + word + "' 长度: " + len);
        
        Map<String, Integer> wordMap = Map.of("Java", 4, "Python", 6, "Go", 2);
        wordMap.forEach(wordLengthPrinter);
        
        System.out.println();
    }

    // ==================== 4. Supplier<T> - 供应接口 ====================
    /**
     * Supplier 不接收参数，返回一个结果
     * 用于"生产"数据，如工厂方法、延迟计算
     * 
     * 典型应用场景：
     * - 对象创建（工厂模式）
     * - 延迟计算/懒加载
     * - 生成随机数
     * - 配置获取
     */
    static void supplierExamples() {
        System.out.println("===== 4. Supplier<T> 供应接口 =====");
        
        // 常量供应
        Supplier<String> greeting = () -> "Hello, Functional Programming!";
        System.out.println("  问候: " + greeting.get());
        
        // 随机数供应
        Supplier<Double> randomSupplier = () -> Math.random();
        System.out.println("  随机数: " + randomSupplier.get());
        
        // 对象创建
        Supplier<StringBuilder> sbFactory = StringBuilder::new;
        StringBuilder sb = sbFactory.get();
        sb.append("通过 Supplier 创建");
        System.out.println("  StringBuilder: " + sb);
        
        // 典型应用：延迟计算
        Supplier<Double> expensiveCalculation = () -> {
            System.out.println("  [执行耗时计算...]");
            return Math.PI * Math.PI;
        };
        // 调用时才执行
        System.out.println("  计算结果: " + expensiveCalculation.get());
        
        // 典型应用：Optional 的 orElseGet
        String nullValue = null;
        String result = Optional.ofNullable(nullValue)
                .orElseGet(() -> "默认值");
        System.out.println("  Optional 默认值: " + result);
        
        // 典型应用：供应流
        Supplier<IntStream> intStreamSupplier = () -> IntStream.range(1, 6);
        System.out.print("  供应的流: ");
        intStreamSupplier.get().forEach(n -> System.out.print(n + " "));
        System.out.println();
        
        System.out.println();
    }

    // ==================== 5. BiFunction<T, U, R> - 双参数转换 ====================
    /**
     * BiFunction 接收两个参数(T, U)，返回一个结果(R)
     * 
     * 典型应用场景：
     * - 两数运算
     * - 字符串拼接
     * - Map 的 compute/merge 操作
     * - 归约操作
     */
    static void biFunctionExamples() {
        System.out.println("===== 5. BiFunction<T, U, R> 双参数转换 =====");
        
        // 数学运算
        BiFunction<Integer, Integer, Integer> add = (a, b) -> a + b;
        BiFunction<Integer, Integer, Integer> multiply = (a, b) -> a * b;
        System.out.println("  3 + 4 = " + add.apply(3, 4));      // 7
        System.out.println("  3 × 4 = " + multiply.apply(3, 4)); // 12
        
        // 字符串操作
        BiFunction<String, String, String> concat = (s1, s2) -> s1 + " " + s2;
        System.out.println("  拼接: " + concat.apply("Hello", "World"));  // Hello World
        
        // 类型转换
        BiFunction<String, Integer, String> repeat = (s, n) -> s.repeat(n);
        System.out.println("  重复: " + repeat.apply("Ha", 3));  // HaHaHa
        
        // 典型应用：计算折扣价格
        BiFunction<Double, Double, Double> applyDiscount = (price, discount) -> 
            price * (1 - discount / 100);
        System.out.println("  原价100，打8折: " + applyDiscount.apply(100.0, 20.0));  // 80.0
        
        // 典型应用：Map 合并
        Map<String, Integer> inventory1 = Map.of("Apple", 5, "Banana", 10);
        Map<String, Integer> inventory2 = Map.of("Apple", 3, "Orange", 7);
        
        Map<String, Integer> merged = new HashMap<>(inventory1);
        inventory2.forEach((key, value) -> 
            merged.merge(key, value, Integer::sum)  // Integer::sum 是 BiFunction
        );
        System.out.println("  合并库存: " + merged);  // {Apple=8, Banana=10, Orange=7}
        
        System.out.println();
    }

    // ==================== 6. UnaryOperator<T> - 一元操作 ====================
    /**
     * UnaryOperator 是 Function 的特化版，输入输出类型相同
     * 
     * 典型应用场景：
     * - 数据格式转换
     * - 数值计算
     * - Stream.map() 同类型转换
     */
    static void unaryOperatorExamples() {
        System.out.println("===== 6. UnaryOperator<T> 一元操作 =====");
        
        // 数值操作
        UnaryOperator<Integer> square = n -> n * n;
        UnaryOperator<Integer> increment = n -> n + 1;
        System.out.println("  5² = " + square.apply(5));    // 25
        System.out.println("  5+1 = " + increment.apply(5)); // 6
        
        // 字符串操作
        UnaryOperator<String> trim = s -> s.trim();
        UnaryOperator<String> toLower = s -> s.toLowerCase();
        System.out.println("  trim: '" + trim.apply("  hello  ") + "'");  // 'hello'
        System.out.println("  toLower: " + toLower.apply("HELLO"));       // hello
        
        // 组合操作
        Function<String, String> trimAndLower = trim.andThen(toLower);
        System.out.println("  组合: '" + trimAndLower.apply("  HELLO  ") + "'");  // 'hello'
        
        // 典型应用：列表元素转换
        List<String> words = Arrays.asList("Hello", "  World  ", "JAVA");
        List<String> normalized = words.stream()
                .map(trim.andThen(toLower))
                .collect(Collectors.toList());
        System.out.println("  规范化: " + normalized);  // [hello, world, java]
        
        System.out.println();
    }

    // ==================== 7. BinaryOperator<T> - 二元操作 ====================
    /**
     * BinaryOperator 是 BiFunction 的特化版，两个输入和输出类型相同
     * 
     * 典型应用场景：
     * - 归约操作 (reduce)
     * - 比较取最大/最小
     * - 累积计算
     */
    static void binaryOperatorExamples() {
        System.out.println("===== 7. BinaryOperator<T> 二元操作 =====");
        
        // 数学运算
        BinaryOperator<Integer> add = (a, b) -> a + b;
        BinaryOperator<Integer> max = Math::max;
        BinaryOperator<Integer> min = Math::min;
        
        System.out.println("  3 + 4 = " + add.apply(3, 4));      // 7
        System.out.println("  max(3,4) = " + max.apply(3, 4));   // 4
        System.out.println("  min(3,4) = " + min.apply(3, 4));   // 3
        
        // 典型应用：归约求和
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
        int sum = numbers.stream()
                .reduce(0, add);  // 0 是初始值
        System.out.println("  归约求和: " + sum);  // 15
        
        // 归约求积
        BinaryOperator<Integer> multiply = (a, b) -> a * b;
        int product = numbers.stream()
                .reduce(1, multiply);
        System.out.println("  归约求积: " + product);  // 120
        
        // 典型应用：找最大值
        int maxVal = numbers.stream()
                .reduce(Integer.MIN_VALUE, max);
        System.out.println("  最大值: " + maxVal);  // 5
        
        // 字符串拼接
        BinaryOperator<String> concat = (s1, s2) -> s1 + s2;
        List<String> words = Arrays.asList("Java", " ", "is", " ", "great");
        String sentence = words.stream()
                .reduce("", concat);
        System.out.println("  拼接: " + sentence);  // Java is great
        
        System.out.println();
    }

    // ==================== 8. 函数组合高级用法 ====================
    /**
     * 函数组合是函数式编程的核心特性
     * 通过组合简单函数构建复杂逻辑
     */
    static void compositionExamples() {
        System.out.println("===== 8. 函数组合高级用法 =====");
        
        // --- Function 组合 ---
        Function<Integer, String> intToStr = Object::toString;
        Function<String, String> addPrefix = s -> "Prefix_" + s;
        Function<Integer, String> intToPrefixedStr = intToStr.andThen(addPrefix);
        System.out.println("  Function组合: " + intToPrefixedStr.apply(42));  // Prefix_42
        
        // --- Predicate 组合 ---
        Predicate<Integer> gt0 = n -> n > 0;
        Predicate<Integer> isEven = n -> n % 2 == 0;
        Predicate<Integer> lt10 = n -> n < 10;
        
        // and: 全部满足
        Predicate<Integer> complex = gt0.and(isEven).and(lt10);
        System.out.println("  8 满足所有条件? " + complex.test(8));   // true
        System.out.println("  12 满足所有条件? " + complex.test(12)); // false (>10)
        
        // or: 满足任一
        Predicate<Integer> anyCondition = gt0.or(isEven);
        System.out.println("  -2 满足任一? " + anyCondition.test(-2));  // true (偶数)
        
        // --- 实际应用：数据处理管道 ---
        Function<String, String> trim = String::trim;
        Function<String, String> lower = String::toLowerCase;
        Function<String, String> removePunct = s -> s.replaceAll("[.,!?]", "");
        
        // 构建处理管道
        Function<String, String> processPipeline = trim
                .andThen(lower)
                .andThen(removePunct);
        
        String raw = "  Hello, World!  ";
        System.out.println("  管道处理: '" + processPipeline.apply(raw) + "'");  // 'hello world'
        
        System.out.println();
    }

    // ==================== 9. Stream API 与函数式接口 ====================
    /**
     * Stream API 大量使用函数式接口
     * 这是函数式编程在实际项目中最常见的应用
     */
    static void streamWithFunctionalInterfaces() {
        System.out.println("===== 9. Stream API 与函数式接口 =====");
        
        List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
        
        // filter - Predicate
        List<Integer> evens = numbers.stream()
                .filter(n -> n % 2 == 0)  // Predicate
                .collect(Collectors.toList());
        System.out.println("  偶数: " + evens);  // [2, 4, 6, 8, 10]
        
        // map - Function
        List<Integer> squares = numbers.stream()
                .map(n -> n * n)  // Function
                .collect(Collectors.toList());
        System.out.println("  平方: " + squares);  // [1, 4, 9, 16, 25, 36, 49, 64, 81, 100]
        
        // forEach - Consumer
        System.out.print("  遍历: ");
        numbers.stream()
                .limit(5)
                .forEach(n -> System.out.print(n + " "));  // 1 2 3 4 5
        System.out.println();
        
        // reduce - BinaryOperator
        int sum = numbers.stream()
                .reduce(0, Integer::sum);  // BinaryOperator
        System.out.println("  求和: " + sum);  // 55
        
        // sorted - Comparator (也是函数式接口)
        List<String> words = Arrays.asList("banana", "apple", "cherry", "date");
        List<String> sorted = words.stream()
                .sorted((a, b) -> a.compareTo(b))  // Comparator
                .collect(Collectors.toList());
        System.out.println("  排序: " + sorted);  // [apple, banana, cherry, date]
        
        // collect - 结合各种转换
        Map<Boolean, List<Integer>> partitioned = numbers.stream()
                .collect(Collectors.partitioningBy(n -> n % 2 == 0));
        System.out.println("  分区(偶/奇): " + partitioned);
        // {true=[2, 4, 6, 8, 10], false=[1, 3, 5, 7, 9]}
        
        System.out.println();
    }

    // ==================== 10. 实际应用案例 ====================
    /**
     * 综合应用：模拟数据处理场景
     */
    static void practicalApplications() {
        System.out.println("===== 10. 实际应用案例 =====");
        
        // --- 案例1：订单处理 ---
        record Order(String id, double amount, String status) {}
        
        List<Order> orders = Arrays.asList(
            new Order("O001", 100.0, "COMPLETED"),
            new Order("O002", 250.0, "PENDING"),
            new Order("O003", 50.0, "COMPLETED"),
            new Order("O004", 180.0, "CANCELLED"),
            new Order("O005", 300.0, "COMPLETED")
        );
        
        // Predicate: 过滤已完成的订单
        Predicate<Order> isCompleted = o -> "COMPLETED".equals(o.status());
        
        // Function: 提取订单金额
        Function<Order, Double> getAmount = Order::amount;
        
        // 计算已完成订单的总金额
        double totalCompleted = orders.stream()
                .filter(isCompleted)
                .mapToDouble(getAmount::apply)
                .sum();
        System.out.println("  已完成订单总额: " + totalCompleted);  // 450.0
        
        // --- 案例2：数据清洗管道 ---
        List<String> rawData = Arrays.asList(
            "  John ", "", "  Alice  ", "BOB", "  ", "charlie  "
        );
        
        // 构建数据处理管道
        Predicate<String> isNotBlank = s -> !s.trim().isEmpty();
        UnaryOperator<String> trim = String::trim;
        UnaryOperator<String> toTitleCase = s -> 
            s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
        
        List<String> cleaned = rawData.stream()
                .filter(isNotBlank)     // 过滤空白
                .map(trim)              // 去两端空格
                .map(toTitleCase)       // 转标题格式
                .collect(Collectors.toList());
        
        System.out.println("  数据清洗: " + cleaned);  
        // [John, Alice, Bob, Charlie]
        
        // --- 案例3：函数式缓存（记忆化） ---
        // 使用 Function 实现简单的缓存
        Function<Integer, Integer> fibonacci = memoizeFibonacci();
        System.out.println("  Fibonacci(10): " + fibonacci.apply(10));  // 55
        System.out.println("  Fibonacci(15): " + fibonacci.apply(15));  // 610
        
        // --- 案例4：条件组合查询 ---
        Predicate<Order> amountGt100 = o -> o.amount() > 100;
        Predicate<Order> statusCompleted = o -> "COMPLETED".equals(o.status());
        
        // 组合条件：金额>100 且 已完成
        Predicate<Order> combined = amountGt100.and(statusCompleted);
        
        List<Order> filtered = orders.stream()
                .filter(combined)
                .collect(Collectors.toList());
        
        System.out.println("  大额已完成订单: " + filtered);
        // [Order[id=O005, amount=300.0, status=COMPLETED]]
        
        System.out.println();
    }
    
    // 带缓存的 Fibonacci
    static Function<Integer, Integer> memoizeFibonacci() {
        Map<Integer, Integer> cache = new HashMap<>();
        cache.put(0, 0);
        cache.put(1, 1);
        
        return n -> {
            if (cache.containsKey(n)) {
                return cache.get(n);
            }
            int result = memoizeFibonacciHelper(n, cache);
            return result;
        };
    }
    
    static int memoizeFibonacciHelper(int n, Map<Integer, Integer> cache) {
        if (cache.containsKey(n)) {
            return cache.get(n);
        }
        int result = memoizeFibonacciHelper(n - 1, cache) + 
                     memoizeFibonacciHelper(n - 2, cache);
        cache.put(n, result);
        return result;
    }
}
