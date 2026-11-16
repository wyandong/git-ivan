package test.com.ivan;

import main.com.ivan.ResultException;

public class ResultExceptionTest {
    
    public static void checkAge(int age){
        if (age < 0) {
            throw new ResultException(500, "年龄不能小于0");
        }
        System.out.println("年龄校验通过：" + age);
    }

    public static void main(String[] args) {
        try {
            checkAge(-1);
        } catch (ResultException e) {
            
            // 专门捕获我们自己定义的异常
            System.out.println("捕获到自定义异常！");
            System.out.println("错误码：" + e.getCode());
            System.out.println("错误信息：" + e.getMsg());
            // e.printStackTrace(); // 打印堆栈，调试用，上线一般不直接打印给前端
        } catch (Exception e) {
            // 兜底：捕获其他所有未知异常
            System.out.println("其他未知异常：" + e.getMessage());
        }
        System.out.println("程序继续往下执行");
    }
}
