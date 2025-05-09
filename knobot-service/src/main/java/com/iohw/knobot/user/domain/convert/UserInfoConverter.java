package com.iohw.knobot.user.domain.convert;

import org.mapstruct.Mapper;

import com.iohw.knobot.user.domain.entity.UserInfoDO;
import com.iohw.knobot.user.domain.vo.response.UserInfoResponse;

/**
 * @author: iohw
 * @date: 2025/5/5 10:26
 * @description:
 */
@Mapper(componentModel = "spring")
public interface UserInfoConverter {
    UserInfoResponse toDto(UserInfoDO userInfoDO);
}
