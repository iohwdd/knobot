package com.iohw.knobot.user.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/7 23:18
 * @description:
 */
@Data
public class SendEmailCommand {
    private Long userId;
    private String to;
}
