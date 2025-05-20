package com.iohw.knobot.hook.interceptor.handler;
import com.iohw.knobot.common.Result;
import com.iohw.knobot.common.exception.BusinessException;
import com.iohw.knobot.common.exception.UnAuthorizedException;

import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import jakarta.validation.ConstraintViolationException;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: iohw
 * @date: 2025/5/5 10:47
 * @description:
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    /**
     * 业务异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(BusinessException.class)
    public Result<Void> handleBusinessException(Exception e) {
        log.warn("业务异常! 原因：{}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 权限校验异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(UnAuthorizedException.class)
    public Result<Void> handleUnAuthorizedException(Exception e) {
        log.warn("权限校验失败! 原因：{}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    /**
     * 未知异常
     *
     * @param e
     * @return
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.warn("未知异常! 原因：{}", e.getMessage(), e);
        return Result.error(e.getMessage());
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValid(MethodArgumentNotValidException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败，原因： {}", msg, e);
        return Result.error("参数校验失败：" + msg);
    }

    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        String msg = e.getBindingResult().getFieldError().getDefaultMessage();
        log.warn("参数校验失败，原因： {}", msg, e);
        return Result.error("参数校验失败：" + msg);
    }

    @ExceptionHandler(ConstraintViolationException.class)
    public Result<Void> handleConstraintViolationException(ConstraintViolationException e) {
        String msg = e.getConstraintViolations().iterator().next().getMessage();
        log.warn("参数校验失败，原因： {}", msg, e);
        return Result.error("参数校验失败：" + msg);
    }
}
