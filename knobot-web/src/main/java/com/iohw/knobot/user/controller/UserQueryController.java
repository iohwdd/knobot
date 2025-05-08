package com.iohw.knobot.user.controller;

import com.iohw.knobot.common.response.Result;
import com.iohw.knobot.user.model.vo.UserDetailInfoVO;
import com.iohw.knobot.user.request.QueryUserDetailInfoRequest;
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
    public Result<UserDetailInfoVO> queryUserDetailInfo(@RequestBody QueryUserDetailInfoRequest request) {
        UserDetailInfoVO userDetailInfoVO = userInfoService.queryUserDetailInfo(request);
        return Result.success(userDetailInfoVO);
    }
}
