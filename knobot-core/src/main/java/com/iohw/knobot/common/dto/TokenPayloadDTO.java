package com.iohw.knobot.common.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/20 15:57
 * @description:
 */
@Data
@Builder
public class TokenPayloadDTO {
    private long userId;
    private String username;
    private String avatarUrl;
}
