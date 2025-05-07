package com.iohw.knobot.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.iohw.knobot.common.ReqContext;
import com.iohw.knobot.common.constant.Constants;
import com.iohw.knobot.common.exception.BusinessException;
import com.iohw.knobot.user.model.UserInfoDO;
import com.iohw.knobot.user.mapper.UserInfoMapper;
import com.iohw.knobot.user.model.convert.UserInfoConverter;
import com.iohw.knobot.user.model.dto.UserInfoDto;
import com.iohw.knobot.user.request.LoginRequest;
import com.iohw.knobot.user.request.ModifyUserInfoRequest;
import com.iohw.knobot.user.request.RegistryRequest;
import com.iohw.knobot.user.service.UserInfoService;
import com.iohw.knobot.utils.IdGeneratorUtil;
import com.iohw.knobot.utils.ThreadLocalUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.iohw.knobot.common.constant.Constants.REQ_CONTEXT;

/**
 * @author: iohw
 * @date: 2025/5/4 22:27
 * @description: 用户信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements UserInfoService {
    private final UserInfoMapper userInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Long createUser(RegistryRequest registryRequest) {
        UserInfoDO user = getUserByUsername(registryRequest.getUsername());
        if(user != null) {
            throw new BusinessException("用户名已存在");
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUsername(registryRequest.getUsername());
        userInfoDO.setPassword(registryRequest.getPassword());
        userInfoDO.setUserId(IdUtil.getSnowflake().nextId());

        userInfoMapper.insert(userInfoDO);
        return userInfoDO.getUserId();
    }

    @Override
    public UserInfoDO getUserById(Long id) {
        return userInfoMapper.selectById(id);
    }

    @Override
    public UserInfoDO getUserByUsername(String username) {

        return userInfoMapper.selectByUsername(username);
    }

    @Override
    public UserInfoDO getUserByEmail(String email) {
        return userInfoMapper.selectByEmail(email);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean updateUserInfo(ModifyUserInfoRequest modifyUserInfoRequest) {
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(modifyUserInfoRequest.getUserId());
        userInfo.setPassword(modifyUserInfoRequest.getNewPassword());
        userInfo.setNickname(modifyUserInfoRequest.getNickname());
        userInfo.setAvatarUrl(modifyUserInfoRequest.getAvatarUrl());

        return userInfoMapper.updateById(userInfo) > 0;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public boolean deleteUser(Long id) {
        return userInfoMapper.deleteById(id) > 0;
    }

    @Override
    public List<UserInfoDO> listUsers() {
        return userInfoMapper.selectList();
    }

    @Override
    public UserInfoDto login(HttpServletRequest req, HttpServletResponse resp, LoginRequest request) {
        UserInfoDO user = userInfoMapper.selectByUsername(request.getUsername());
        // 用户不存在或密码错误
        if (user == null || !request.getPassword().equals(user.getPassword())) {
            return null;
        }

        // 删除旧token
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(TOKEN)) {
                stringRedisTemplate.delete(cookie.getValue());
                // 删除旧cookie
                Cookie del = new Cookie(cookie.getName(), null);
                del.setMaxAge(0);
                del.setPath("/");
                resp.addCookie(del);
                break;
            }
        }

        // redis实现共享session
        String token = UUID.randomUUID().toString();

        if(Boolean.FALSE.equals(stringRedisTemplate.hasKey(token))) {
            stringRedisTemplate.opsForValue().set(token, String.valueOf(user.getUserId()),7, TimeUnit.DAYS);
        }

        // 设置上下文用户信息
        ReqContext reqContext = ReqContext.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .avatarUrl(user.getAvatarUrl())
                .nickName(user.getNickname())
                .build();
        ThreadLocalUtils.set(REQ_CONTEXT, reqContext);

        UserInfoDto dto = UserInfoConverter.INSTANCE.toDto(user);

        // 响应添加cookie
        Cookie cookie = new Cookie(TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setSecure(true);
        cookie.setMaxAge(7 * 24 * 60 * 60);
        cookie.setPath("/");
        resp.addCookie(cookie);
        resp.addCookie(new Cookie(TOKEN, token));

        return dto;
    }

    @Override
    public void logout(HttpServletResponse resp, HttpServletRequest req) {
        Cookie[] cookies = req.getCookies();
        for (Cookie cookie : cookies) {
            if(cookie.getName().equals(TOKEN)) {
                stringRedisTemplate.delete(cookie.getValue());
            }
        }

        Cookie cookie = new Cookie(TOKEN, null);
        cookie.setMaxAge(0);
        cookie.setPath("/");
        resp.addCookie(cookie);
    }
}
