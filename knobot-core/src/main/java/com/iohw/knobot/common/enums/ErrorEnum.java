package com.iohw.knobot.common.enums;

/**
 * @author: iohw
 * @date: 2025/5/4 22:52
 * @description:
 */
public enum ErrorEnum {
    USER_NOT_EXIST("用户不存在");

    private String desc;
    ErrorEnum(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return this.desc;
    }
}
