package main.com.ivan;
import java.util.*;
import java.util.stream.Collectors;
/**
 * Java Stream API 综合示例
 * Stream API 是 Java 8 引入的函数式编程工具，用于以声明式方式处理集合数据
 * 核心特点：函数式风格、链式调用、惰性求值、并行处理
 */
public class streamProgramming {

    /**
     * 商品数据模型 —— 整个示例的数据源
     * 使用静态内部类方便在 main 方法中直接引用
     */
    static class Product {
        private int id;        // 商品编号
        private String name;    // 商品名称
        private String category;// 商品分类（Electronics / Furniture 等）
        private double price;   // 商品价格

        public Product(int id, String name, String category, double price) {
            this.id = id;
            this.name = name;
            this.category = category;
            this.price = price;
        }

        // Getter 方法 —— Stream 中通过方法引用 Product::getCategory 调用
        public String getCategory() { return category; }
        public double getPrice() { return price; }
        public String getName() { return name; }
        
        @Override
        public String toString() {
            return name + " (" + category + ") - $" + String.format("%.2f", price);
        }
    }

    public static void main(String[] args) {
        // 准备演示数据：6 个商品，分布在 3 个不同分类中
        List<Product> products = Arrays.asList(
            new Product(1, "Laptop", "Electronics", 999.99),
            new Product(2, "Smartphone", "Electronics", 699.50),
            new Product(3, "Coffee Maker", "Home Appliances", 49.99),
            new Product(4, "Desk Chair", "Furniture", 150.00),
            new Product(5, "Headphones", "Electronics", 199.99),
            new Product(6, "Blender", "Home Appliances", 35.00)
        );

        // 依次演示 7 种 Stream 操作
        System.out.println("--- 1. Filtering ---");
        filterExample(products);

        System.out.println("\n--- 2. Mapping ---");
        mapExample(products);

        System.out.println("\n--- 3. Sorting ---");
        sortExample(products);

        System.out.println("\n--- 4. Reduction & Aggregation ---");
        reductionExample(products);

        System.out.println("\n--- 5. Grouping ---");
        groupingExample(products);

        System.out.println("\n--- 6. FlatMap ---");
        flatMapExample();
        
        System.out.println("\n--- 7. Distinct, Limit, and Skip ---");
        distinctLimitSkipExample(products);
    }

    /**
     * 1️⃣ 过滤操作 filter()
     * 中间操作：返回包含满足条件元素的新 Stream
     * 示例：筛选出 "Electronics" 分类 且 价格 > 500 的商品
     * 
     * 链式调用可以叠加多个 filter，语义更清晰
     */
    private static void filterExample(List<Product> products) {
        products.stream()                                    // 1. 把 List 转成 Stream
            .filter(p -> "Electronics".equals(p.getCategory())) // 2. 第一层过滤：按分类
            .filter(p -> p.getPrice() > 500.0)               // 3. 第二层过滤：按价格
            .forEach(System.out::println);                    // 4. 终结操作：逐个打印
    }

    /**
     * 2️⃣ 映射操作 map()
     * 中间操作：将 Stream 中每个元素 T 转换为另一种类型 R，形成 Stream<R>
     * 
     * 两种典型用法：
     *   a) 提取字段 —— Product → String（商品名）
     *   b) 数据变换 —— Product → Double（折扣价）
     * 
     * collect(Collectors.toList()) 是把 Stream 收集回 List 的标准写法
     */
    private static void mapExample(List<Product> products) {
        // a) 提取特定字段 —— 使用方法引用 Product::getName
        List<String> productNames = products.stream()
            .map(Product::getName)
            .collect(Collectors.toList());
        System.out.println("Names: " + productNames);

        // b) 变换数据 —— 给所有商品打 9 折
        List<Double> discountedPrices = products.stream()
            .map(p -> p.getPrice() * 0.9)
            .collect(Collectors.toList());
        System.out.println("Discounted Prices: " + discountedPrices);
    }

    /**
     * 3️⃣ 排序操作 sorted()
     * 中间操作：对 Stream 中的元素进行排序
     * 
     * Comparator.comparingDouble() 接受一个 ToDoubleFunction，按指定 double 字段排序
     * .reversed() 将排序结果反转，实现降序
     */
    private static void sortExample(List<Product> products) {
        products.stream()
            .sorted(Comparator.comparingDouble(Product::getPrice).reversed()) // 按价格降序
            .forEach(System.out::println);
    }

    /**
     * 4️⃣ 归约与聚合 Reduction & Aggregation
     * 终结操作：将 Stream 聚合为单个值
     * 
     * mapToDouble() 把 Stream<Product> 转为 DoubleStream（原始类型流），
     * 这样可以直接调用 .sum()、.average() 等高效聚合方法
     * 
     * Optional<T>：max() 返回 Optional，因为空流没有最大值
     * ifPresent() 是安全消费 Optional 的标准方式
     */
    private static void reductionExample(List<Product> products) {
        // 计算所有商品的总价值
        double totalValue = products.stream()
            .mapToDouble(Product::getPrice)
            .sum();
        System.out.println("Total Value: $" + totalValue);

        // 找出最贵的商品
        Optional<Product> mostExpensive = products.stream()
            .max(Comparator.comparingDouble(Product::getPrice));
        mostExpensive.ifPresent(p -> System.out.println("Most Expensive: " + p));
    }

    /**
     * 5️⃣ 分组操作 Collectors.groupingBy()
     * 这是最强大的收集器之一，类似 SQL 中的 GROUP BY
     * 
     * a) 简单分组：按 category 把商品分到 Map<String, List<Product>>
     * b) 分组+下游收集器：按 category 分组，同时计算每组的平均价格
     *    第二个参数 "下游收集器" 决定每组如何聚合
     */
    private static void groupingExample(List<Product> products) {
        // a) 按分类分组，得到 Map<分类, 该分类下所有商品列表>
        Map<String, List<Product>> groupedByCategory = products.stream()
            .collect(Collectors.groupingBy(Product::getCategory));
        
        groupedByCategory.forEach((k, v) -> System.out.println(k + ": " + v));

        // b) 按分类分组，同时计算每组的平均价格（分组 + 下游收集器）
        Map<String, Double> avgPriceByCategory = products.stream()
            .collect(Collectors.groupingBy(
                Product::getCategory,                        // 分类键
                Collectors.averagingDouble(Product::getPrice) // 下游收集器：求平均
            ));
        System.out.println("Average Prices: " + avgPriceByCategory);
    }

    /**
     * 6️⃣ FlatMap：扁平化映射
     * 中间操作：把 Stream<List<T>> 「拍扁」成 Stream<T>
     * 
     * 与 map() 的区别：
     *   map()   —— 一对一转换，Stream<A> → Stream<B>
     *   flatMap() —— 一对多拍扁，Stream<List<A>> → Stream<A>
     * 
     * 这里 Collection::stream 把每个内层 List 展开为一个 Stream，
     * flatMap 再把所有这些子 Stream 合并成一个大 Stream
     */
    private static void flatMapExample() {
        // 嵌套列表：三个子列表，每个子列表里装若干水果
        List<List<String>> listOfLists = Arrays.asList(
            Arrays.asList("Apple", "Banana"),
            Arrays.asList("Orange", "Grape"),
            Arrays.asList("Peach")
        );

        // flatMap 把三层结构拍平为一层 List<String>
        List<String> flatList = listOfLists.stream()   // Stream<List<String>>
            .flatMap(Collection::stream)                // → Stream<String>
            .collect(Collectors.toList());               // → List<String>
        
        System.out.println("Flattened List: " + flatList);
    }

    /**
     * 7️⃣ 切片操作：distinct、limit、skip
     * 
     * distinct() —— 中间操作：去重（依赖 equals/hashCode）
     * limit(n)   —— 中间操作：截取前 n 个元素，类似 SQL 的 LIMIT
     * skip(n)    —— 中间操作：跳过前 n 个元素，常用于分页
     * 
     * 分页公式：skip((page - 1) * pageSize).limit(pageSize)
     */
    private static void distinctLimitSkipExample(List<Product> products) {
        // 先 map 提取分类名，再 distinct 去重，得到所有不重复的分类
        List<String> distinctCategories = products.stream()
            .map(Product::getCategory)
            .distinct()
            .collect(Collectors.toList());
        System.out.println("Distinct Categories: " + distinctCategories);

        // 模拟分页：跳过第 1 条，取接下来的 2 条
        List<Product> paginatedProducts = products.stream()
            .skip(1)        // 跳过索引 0 的元素
            .limit(2)       // 最多取 2 个
            .collect(Collectors.toList());
        System.out.println("Paginated (skip 1, limit 2): " + paginatedProducts);
    }
}