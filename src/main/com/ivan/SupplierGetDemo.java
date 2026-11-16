package main.com.ivan;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class SupplierGetDemo {
    public static void main(String[] args) {
        
        List<String> list = List.of("apple","banana","apple");

        // ✅ 自定义Supplier，get里面加打印！
        Supplier<LinkedHashSet<String>> myFactory = new Supplier<LinkedHashSet<String>>() {
            @Override
            public LinkedHashSet<String> get() {
                System.out.println("✅ Supplier 的 get() 方法被调用了！新建 LinkedHashSet");
                return new LinkedHashSet<>();
            }
        };

        LinkedHashSet<String> res = list.stream()
                .collect(Collectors.toCollection(myFactory));

        System.out.println("最终集合：" + res);
    }
}

