package com.iohw.knobot.user.service;

import com.iohw.knobot.user.domain.entity.UserInfoDO;
import com.iohw.knobot.user.domain.vo.request.BindEmailCommand;
import com.iohw.knobot.user.domain.vo.request.LoginCommand;
import com.iohw.knobot.user.domain.vo.request.ModifyUserInfoCommand;
import com.iohw.knobot.user.domain.vo.request.QueryUserDetailInfoRequest;
import com.iohw.knobot.user.domain.vo.request.RegistryCommand;
import com.iohw.knobot.user.domain.vo.request.SendEmailCommand;
import com.iohw.knobot.user.domain.vo.response.UserInfoResponse;
import com.iohw.knobot.user.domain.vo.response.UserDetailInfoResp;

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
    Long createUser(RegistryCommand registryCommand);

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
     * @param modifyUserInfoCommand 用户信息
     */
    void updateUserInfo(ModifyUserInfoCommand modifyUserInfoCommand);

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
    UserInfoResponse login(HttpServletRequest req, HttpServletResponse resp, LoginCommand request);

    void logout(HttpServletResponse resp, HttpServletRequest req);

    /**
     * 查询用户详情信息
     * @param request
     * @return
     */
    UserDetailInfoResp queryUserDetailInfo(QueryUserDetailInfoRequest request);

    /**
     * 绑定/换绑 邮箱
     * @param bindEmailCommand
     */
    void bindEmail(BindEmailCommand bindEmailCommand);

    /**
     * 发送邮箱验证码
     * @param sendEmailCommand
     */
    void sendEmail(SendEmailCommand sendEmailCommand);
}
