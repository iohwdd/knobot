package com.iohw.knobot.user.controller;

import com.iohw.knobot.common.Result;
import com.iohw.knobot.user.domain.vo.request.BindEmailCommand;
import com.iohw.knobot.user.domain.vo.request.LoginCommand;
import com.iohw.knobot.user.domain.vo.request.ModifyUserInfoCommand;
import com.iohw.knobot.user.domain.vo.request.RegistryCommand;
import com.iohw.knobot.user.domain.vo.request.SendEmailCommand;
import com.iohw.knobot.user.domain.vo.response.UserInfoResponse;
import com.iohw.knobot.user.service.UserInfoService;

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

    private final UserInfoService userInfoService;


    @PostMapping("/login")
    public Result<UserInfoResponse> login(HttpServletRequest req, HttpServletResponse resp, @RequestBody
    LoginCommand request) {
        UserInfoResponse userInfoResponse = userInfoService.login(req, resp, request);
        if(userInfoResponse == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(userInfoResponse);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest req, HttpServletResponse resp) {
        userInfoService.logout(resp, req);
        return Result.success("登出成功");
    }

    @PostMapping("/registry")
    public Result<Void> registry(@RequestBody RegistryCommand request) {
        userInfoService.createUser(request);
        return Result.success("注册成功");
    }

    @PostMapping("/sendEmail")
    public Result<Void> sendEmail(@RequestBody SendEmailCommand sendEmailCommand) {

        userInfoService.sendEmail(sendEmailCommand);
        return Result.success();
    }

    @PostMapping(value = "/modifyInfo")
    public Result<Void> modify(ModifyUserInfoCommand modifyUserInfoCommand) {
        userInfoService.updateUserInfo(modifyUserInfoCommand);
        return Result.success();
    }

    @PostMapping("/bindEmail")
    public Result<Void> bindEmail(@RequestBody BindEmailCommand bindEmailCommand) {
        userInfoService.bindEmail(bindEmailCommand);
        return Result.success();
    }
}