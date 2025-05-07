package com.iohw.knobot.user.model.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/5 10:23
 * @description:
 */
@Data
@Builder
public class UserInfoDto {
    private Long userId;
    private String userName;
    private String nickName;
    private String avatarUrl;
}
