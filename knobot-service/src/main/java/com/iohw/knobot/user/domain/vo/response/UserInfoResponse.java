package com.iohw.knobot.user.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/5 10:23
 * @description:
 */
@Data
@Builder
public class UserInfoResponse {
    private Long userId;
    private String userName;
    private String nickName;
    private String avatarUrl;
}
