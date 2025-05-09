package com.iohw.knobot.common;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/4 23:23
 * @description:
 */
@Data
@Builder
public class ReqContext {
    private Long userId;
    private String userName;
    private String nickName;
    private String avatarUrl;

}
