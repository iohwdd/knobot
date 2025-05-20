package com.iohw.knobot.common.exception;

import com.iohw.knobot.common.enums.ErrorEnum;

/**
 * @author: iohw
 * @date: 2025/5/20 16:07
 * @description:
 */
public class UnAuthorizedException extends RuntimeException {
    /**
     * 错误码
     */
    protected Integer errorCode;

    /**
     * 错误信息
     */
    protected String errorMsg;

    public UnAuthorizedException(String message) {
        super(message);
    }

    public UnAuthorizedException(ErrorEnum error) {
        super(error.getDesc());
        this.errorCode = error.getCode();
        this.errorMsg = error.getDesc();
    }
}
