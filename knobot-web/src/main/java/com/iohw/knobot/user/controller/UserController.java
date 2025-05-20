package com.iohw.knobot.user.controller;

import com.iohw.knobot.common.Result;
import com.iohw.knobot.user.domain.vo.request.BindEmailCommand;
import com.iohw.knobot.user.domain.vo.request.LoginCommand;
import com.iohw.knobot.user.domain.vo.request.ModifyUserInfoCommand;
import com.iohw.knobot.user.domain.vo.request.RegistryCommand;
import com.iohw.knobot.user.domain.vo.request.SendEmailCommand;
import com.iohw.knobot.user.domain.vo.response.UserInfoResponse;
import com.iohw.knobot.user.service.IUserInfoService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: iohw
 * @date: 2025/5/4 22:00
 * @description: 用户控制器
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final IUserInfoService IUserInfoService;


    @PostMapping("/login")
    public Result<UserInfoResponse> login(HttpServletRequest req, HttpServletResponse resp, @RequestBody
    LoginCommand request) {
        UserInfoResponse userInfoResponse = IUserInfoService.login(req, resp, request);
        if(userInfoResponse == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(userInfoResponse);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest req, HttpServletResponse resp) {
        IUserInfoService.logout(resp, req);
        return Result.success("登出成功");
    }

    @PostMapping("/registry")
    public Result<Void> registry(@RequestBody RegistryCommand request) {
        IUserInfoService.createUser(request);
        return Result.success("注册成功");
    }

    @PostMapping("/sendEmail")
    public Result<Void> sendEmail(@RequestBody SendEmailCommand sendEmailCommand) {

        IUserInfoService.sendEmail(sendEmailCommand);
        return Result.success();
    }

    @PostMapping(value = "/modifyInfo")
    public Result<Void> modify(ModifyUserInfoCommand modifyUserInfoCommand) {
        IUserInfoService.updateUserInfo(modifyUserInfoCommand);
        return Result.success();
    }

    @PostMapping("/bindEmail")
    public Result<Void> bindEmail(@RequestBody BindEmailCommand bindEmailCommand) {
        IUserInfoService.bindEmail(bindEmailCommand);
        return Result.success();
    }
}