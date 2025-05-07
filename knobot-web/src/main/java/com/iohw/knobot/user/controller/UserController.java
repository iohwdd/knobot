package com.iohw.knobot.user.controller;

import com.iohw.knobot.chat.tool.SendEmailTool;
import com.iohw.knobot.common.response.Result;
import com.iohw.knobot.user.model.dto.UserInfoDto;
import com.iohw.knobot.user.request.LoginRequest;
import com.iohw.knobot.user.request.ModifyUserInfoRequest;
import com.iohw.knobot.user.request.RegistryRequest;
import com.iohw.knobot.user.request.SendEmailRequest;
import com.iohw.knobot.user.service.UserInfoService;
import com.iohw.knobot.utils.EmailUtil;
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
    private final EmailUtil emailUtil;
    private final UserInfoService userInfoService;

    @PostMapping("/login")
    public Result<UserInfoDto> login(HttpServletRequest req, HttpServletResponse resp, @RequestBody LoginRequest request) {
        UserInfoDto userInfoDto = userInfoService.login(req, resp, request);
        if(userInfoDto == null) {
            return Result.error("账号或密码错误");
        }
        return Result.success(userInfoDto);
    }

    @PostMapping("/logout")
    public Result<Void> logout(HttpServletRequest req, HttpServletResponse resp) {
        userInfoService.logout(resp, req);
        return Result.success("登出成功");
    }

    @PostMapping("/registry")
    public Result<Void> registry(@RequestBody RegistryRequest request) {
        userInfoService.createUser(request);
        return Result.success("注册成功");
    }

    @PostMapping("/sendEmail")
    public Result<Void> sendEmail(@RequestBody SendEmailRequest sendEmailRequest) {
        emailUtil.sendCodeVerifyEmail(sendEmailRequest.getTo(), sendEmailRequest.getEmail());
        return Result.success();
    }

    @PostMapping("/modifyInfo")
    public Result<Void> modify(@RequestBody ModifyUserInfoRequest modifyUserInfoRequest) {
        if(userInfoService.updateUserInfo(modifyUserInfoRequest)) {
            return Result.success("更新成功");
        }
        return Result.error("更新失败");
    }
}