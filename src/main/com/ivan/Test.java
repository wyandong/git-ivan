package main.com.ivan;

// 自定义受检异常
class BusinessException extends Exception {
    public BusinessException(String msg) {
        super(msg);
    }
}

public class Test {
    // throws：声明该方法可能抛出BusinessException，交给调用方
    public static void login(String name) throws BusinessException {
        if ("admin".equals(name)) {
            // throw：手动抛出异常实例
            throw new BusinessException("用户名不能是admin");
        }
    }

    public static void main(String[] args) {
        try {
            login("admin");
        } catch (BusinessException e) {
            System.out.println("捕获业务异常：" + e.getMessage());
        }
    }
}
