package com.iohw.knobot.user.domain.vo.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/8 11:32
 * @description:
 */
@Data
public class QueryUserDetailInfoRequest {
    @NotNull
    private Long userId;
}
