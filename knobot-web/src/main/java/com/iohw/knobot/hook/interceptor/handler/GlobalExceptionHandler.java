package com.iohw.knobot.hook.interceptor.handler;
import com.iohw.knobot.common.Result;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;

/**
 * @author: iohw
 * @date: 2025/5/5 10:47
 * @description:
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        e.printStackTrace();
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        e.printStackTrace();
        return Result.error("参数校验失败：" + msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        e.printStackTrace();
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.error("参数校验失败：" + msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        e.printStackTrace();
        String msg = e.getConstraintViolations().iterator().next().getMessage();
        return Result.error("参数校验失败：" + msg);
    }
}
