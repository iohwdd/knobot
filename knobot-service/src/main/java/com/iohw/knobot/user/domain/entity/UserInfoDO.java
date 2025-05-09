package com.iohw.knobot.user.domain.entity;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * @author: iohw
 * @date: 2025/5/4 22:27
 * @description: 用户信息实体类
 */
@Data
public class UserInfoDO {
    /**
     * 主键ID
     */
    private Long userId;

    /**
     * 用户名
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    /**
     * 邮箱
     */
    private String email;

    /**
     * 昵称
     */
    private String nickname;
    /**
     * 个性签名
     */
    private String description;
    /**
     * 头像URL
     */
    private String avatarUrl;

    /**
     * 创建时间
     */
    private LocalDateTime createTime;

    /**
     * 更新时间
     */
    private LocalDateTime updateTime;
}