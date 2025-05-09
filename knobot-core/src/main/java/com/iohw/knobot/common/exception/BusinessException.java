package com.iohw.knobot.common.exception;

/**
 * @author: iohw
 * @date: 2025/5/5 12:54
 * @description:
 */
public class BusinessException extends RuntimeException {
    public BusinessException(String message) {
        super(message);
    }
}
