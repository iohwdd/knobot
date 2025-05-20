package com.iohw.knobot.user.controller;

import com.iohw.knobot.common.Result;
import com.iohw.knobot.user.domain.vo.response.UserDetailInfoResp;
import com.iohw.knobot.user.domain.vo.request.QueryUserDetailInfoRequest;
import com.iohw.knobot.user.service.IUserInfoService;

import lombok.RequiredArgsConstructor;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author: iohw
 * @date: 2025/5/8 11:26
 * @description:
 */
@RestController
@RequestMapping("/user-query")
@RequiredArgsConstructor
public class UserQueryController {
    private final IUserInfoService IUserInfoService;

    @PostMapping("/queryUserDetailInfo")
    public Result<UserDetailInfoResp> queryUserDetailInfo(@Validated @RequestBody QueryUserDetailInfoRequest request) {
        UserDetailInfoResp userDetailInfoResp = IUserInfoService.queryUserDetailInfo(request);
        return Result.success(userDetailInfoResp);
    }
}
