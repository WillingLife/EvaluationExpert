package com.smartcourse.result;

import lombok.Data;

/**
 * 后端统一返回结果
 */
@Data
public class Result {
    private Integer code; // 编码：0成功，其它数字为失败
    private String msg; // 错误信息
    private Object data; // 数据

    public static Result success() {
        Result result = new Result();
        result.code = 0;
        return result;
    }

    public static Result success(Object data) {
        Result result = new Result();
        result.data = data;
        result.code = 0;
        return result;
    }

    public static Result error(String msg) {
        Result result = new Result();
        result.msg = msg;
        result.code = 1;
        return result;
    }

}