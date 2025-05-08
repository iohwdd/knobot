package com.iohw.knobot.user.service;

import com.iohw.knobot.user.model.UserInfoDO;
import com.iohw.knobot.user.model.dto.UserInfoDto;
import com.iohw.knobot.user.model.vo.UserDetailInfoVO;
import com.iohw.knobot.user.request.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/5/4 22:27
 * @description: 用户信息服务接口
 */
public interface UserInfoService {
    String TOKEN = "token";
    /**
     * 创建用户
     * @return 用户ID
     */
    Long createUser(RegistryRequest registryRequest);

    /**
     * 根据ID获取用户信息
     * @param id 用户ID
     * @return 用户信息
     */
    UserInfoDO getUserById(Long id);

    /**
     * 根据用户名获取用户信息
     * @param username 用户名
     * @return 用户信息
     */
    UserInfoDO getUserByUsername(String username);

    /**
     * 根据邮箱获取用户信息
     * @param email 邮箱
     * @return 用户信息
     */
    UserInfoDO getUserByEmail(String email);

    /**
     * 更新用户信息
     * @param modifyUserInfoRequest 用户信息
     */
    void updateUserInfo(ModifyUserInfoRequest modifyUserInfoRequest);

    /**
     * 删除用户
     * @param id 用户ID
     * @return 是否成功
     */
    boolean deleteUser(Long id);

    /**
     * 获取用户列表
     * @return 用户列表
     */
    List<UserInfoDO> listUsers();

    /**
     * 用户登录
     * @return 用户信息，登录失败返回null
     */
    UserInfoDto login(HttpServletRequest req, HttpServletResponse resp, LoginRequest request);

    void logout(HttpServletResponse resp, HttpServletRequest req);

    /**
     * 查询用户详情信息
     * @param request
     * @return
     */
    UserDetailInfoVO queryUserDetailInfo(QueryUserDetailInfoRequest request);

    /**
     * 绑定/换绑 邮箱
     * @param bindEmailRequest
     */
    void bindEmail(BindEmailRequest bindEmailRequest);

    /**
     * 发送邮箱验证码
     * @param sendEmailRequest
     */
    void sendEmail(SendEmailRequest sendEmailRequest);
}
