package com.iohw.knobot.hook.interceptor.handler;

import com.iohw.knobot.common.Result;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * @author: iohw
 * @date: 2025/5/5 10:47
 * @description:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        return Result.error(e.getMessage());
    }
}
