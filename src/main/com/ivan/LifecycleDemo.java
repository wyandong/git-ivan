package main.com.ivan;

public class LifecycleDemo {

    // ============ 类级别（类初始化阶段） ============
    // ② 准备阶段：JVM 给 static 字段分配内存并置零值 -> qty = 0（这里不执行赋值）
    public static int qty = 100;        // ③ 初始化阶段：执行这段赋值，qty 变为 100

    static {
        System.out.println("[1] 类加载&连接&初始化：static 代码块被执行");  // ③
        System.out.println("[1]   此时 static 字段 qty = " + qty);
    }

    // ============ 对象级别（每次 new 都会执行） ============
    // ④ 实例字段初始化器：new 时、构造器方法体之前执行
    private String name = "实例字段默认值";
    private int age = 18;              // ④

    // ⑤ 构造器
    public LifecycleDemo(String name) {
        this.name = name;              // ⑤
        System.out.println("[2] 对象创建：构造器执行, name=" + this.name
                + ", age=" + this.age);
    }

    // ============ 入口 ============
    public static void main(String[] args) {
        System.out.println("main 开始，尚未触发使用 LifecycleDemo 的静态方法/字段");
        System.out.println("main 中先打印：访问静态常量（不触发初始化） charConstant="); 
        // 注意：下面 new 才真正触发

        // 第一次 new：触发 类初始化(一次性) + 对象创建
        LifecycleDemo o1 = new LifecycleDemo("对象1");

        // 第二次 new：类已初始化过，只执行对象创建
        LifecycleDemo o2 = new LifecycleDemo("对象2");

        System.out.println("最终 qty=" + qty);
        System.out.println("o1.name=" + o1.name + ", o2.name=" + o2.name);
    }
}
