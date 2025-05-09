package com.iohw.knobot.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/13 23:13
 * @description:
 */
@Data
@Builder
public class Result<T> {
    private String code;
    private String msg;
    private T data;

    public static <T> Result<T> success(T data) {
        return Result.<T>builder().code("200").data(data).build();
    }

    public static <T> Result<T> success() {
        return Result.<T>builder().code("200").data(null).build();
    }

    public static <T> Result<T> success(String msg) {
        return Result.<T>builder().code("200").msg(msg).build();
    }

    public static <T> Result<T> error(String msg) {
        return Result.<T>builder().code("500").msg(msg).build();
    }
}
