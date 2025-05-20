package com.iohw.knobot.common.exception;

import com.iohw.knobot.common.enums.ErrorEnum;

/**
 * @author: iohw
 * @date: 2025/5/5 12:54
 * @description:
 */
public class BusinessException extends RuntimeException {
    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public BusinessException(String message) {
        super(message);
    }

    public BusinessException(ErrorEnum errorEnum) {
        super(errorEnum.getDesc());
        this.errorCode = errorEnum.getCode();
        this.errorMsg = errorEnum.getDesc();
    }
}
