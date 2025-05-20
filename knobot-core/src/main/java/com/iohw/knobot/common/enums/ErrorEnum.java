package com.iohw.knobot.common.enums;

import lombok.Getter;

/**
 * @author: iohw
 * @date: 2025/5/4 22:52
 * @description:
 */
public enum ErrorEnum {
    USER_NOT_EXIST(505,"用户不存在"),
    ACCESS_TOKEN_INVALID( 401,"access token无效"),
    REFRESH_TOKEN_INVALID(400, "refresh token无效"),


    ;
    @Getter
    private int code;
    @Getter
    private String desc;
    ErrorEnum(int code,String desc) {
        this.desc = desc;
        this.code = code;
    }

}
