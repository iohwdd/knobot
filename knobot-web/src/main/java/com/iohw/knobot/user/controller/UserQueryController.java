package com.iohw.knobot.user.controller;

import com.iohw.knobot.common.Result;
import com.iohw.knobot.user.domain.vo.response.UserDetailInfoResp;
import com.iohw.knobot.user.domain.vo.request.QueryUserDetailInfoRequest;
import com.iohw.knobot.user.service.UserInfoService;
import lombok.RequiredArgsConstructor;
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
    private final UserInfoService userInfoService;

    @PostMapping("/queryUserDetailInfo")
    public Result<UserDetailInfoResp> queryUserDetailInfo(@RequestBody QueryUserDetailInfoRequest request) {
        UserDetailInfoResp userDetailInfoResp = userInfoService.queryUserDetailInfo(request);
        return Result.success(userDetailInfoResp);
    }
}
