package com.iohw.knobot.user.domain.vo.response;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/20 22:06
 * @description:
 */
@Data
@Builder
public class RefreshTokenResponse {
    private String accessToken;
}
