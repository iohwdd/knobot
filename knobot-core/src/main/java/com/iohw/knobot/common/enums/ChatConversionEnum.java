package com.iohw.knobot.common.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @author: iohw
 * @date: 2025/4/14 22:05
 * @description:
 */
@AllArgsConstructor
public enum ChatConversionEnum {
    NORMAL(0, "正常"),
    DELETED(1, "已删除");
    @Getter
    private int status;
    private String desc;

}
