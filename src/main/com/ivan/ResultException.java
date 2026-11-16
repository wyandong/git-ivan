package main.com.ivan;

public class ResultException extends RuntimeException {

    // 错误码
    private Integer code;
    // 错误消息
    private String msg;

    public ResultException(Integer code, String msg) {
        super(msg);
        this.code = code;
        this.msg = msg;
    }

    // getter
    public Integer getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    

    public static void main(String[] args){
        int age = -5;

        if(age < 0){
            throw new ResultException(500, "年龄不能小于0");
        }else{
            System.out.println("年龄合法");
        }

    }
}

