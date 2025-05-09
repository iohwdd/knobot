package com.iohw.knobot.user.domain.vo.request;

import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/5/4 22:19
 * @description:
 */
@Data
public class RegistryCommand {
    private String username;
    private String password;
}
