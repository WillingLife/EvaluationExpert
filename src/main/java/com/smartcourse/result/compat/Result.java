package com.smartcourse.result.compat;

import lombok.Data;
import org.apache.ibatis.type.Alias;

@Data
@Alias("CompatResult")
public class Result<T> {
    private Integer code;
    private String msg;   // 错误信息
    private T data;       // 泛型数据

    /* ---------- 静态工厂方法 ---------- */

    // 成功：无数据
    public static <T> Result<T> success() {
        Result<T> result = new Result<>();
        result.code = 200;
        return result;
    }

    // 成功：携带数据
    public static <T> Result<T> success(T data) {
        Result<T> result = new Result<>();
        result.code = 200;
        result.data = data;
        return result;
    }

    // 失败：仅错误信息
    public static <T> Result<T> error(String msg) {
        Result<T> result = new Result<>();
        result.code = 500;
        result.msg = msg;
        return result;
    }
    public static <T> Result<T> error(Integer code, String msg) {
        Result<T> result = new Result<>();
        result.code = code;
        result.msg = msg;
        return result;
    }
}