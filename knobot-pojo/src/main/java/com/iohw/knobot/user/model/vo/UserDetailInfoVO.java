package com.iohw.knobot.user.model.vo;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/8 11:31
 * @description:
 */
@Data
@Builder
public class UserDetailInfoVO {
    private String username;
    private String nickName;
    private String avatarUrl;
    private String description;
    private long joinDays;
    private String email;
}
