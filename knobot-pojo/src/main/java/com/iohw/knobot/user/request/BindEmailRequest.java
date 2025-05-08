package com.iohw.knobot.user.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/8 15:29
 * @description:
 */
@Data
public class BindEmailRequest {
    private long userId;
    private String newEmail;
    private String code;
}
