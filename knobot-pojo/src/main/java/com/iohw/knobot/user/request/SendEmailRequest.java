package com.iohw.knobot.user.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/7 23:18
 * @description:
 */
@Data
public class SendEmailRequest {
    private String to;
    private String email;
}
