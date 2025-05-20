package com.iohw.knobot.user.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/8 11:31
 * @description:
 */
@Data
@Builder
public class UserDetailInfoResp {
    private String username;
    private String avatarUrl;
    private String description;
    private long joinDays;
    private String email;
}
