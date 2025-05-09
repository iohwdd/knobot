package com.iohw.knobot.user.domain.vo.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: iohw
 * @date: 2025/5/4 22:20
 * @description:
 */
@Data
public class ModifyUserInfoCommand {
    private Long userId;
    private String newPassword;
    private String nickname;
    private String description;
    private MultipartFile avatar;
}
