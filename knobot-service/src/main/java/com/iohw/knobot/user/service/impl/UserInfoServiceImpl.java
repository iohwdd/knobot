package com.iohw.knobot.user.service.impl;

import cn.hutool.core.util.IdUtil;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iohw.knobot.common.ReqContext;
import com.iohw.knobot.common.exception.BusinessException;
import com.iohw.knobot.user.domain.entity.UserInfoDO;
import com.iohw.knobot.user.domain.vo.request.BindEmailCommand;
import com.iohw.knobot.user.domain.vo.request.LoginCommand;
import com.iohw.knobot.user.domain.vo.request.ModifyUserInfoCommand;
import com.iohw.knobot.user.domain.vo.request.QueryUserDetailInfoRequest;
import com.iohw.knobot.user.domain.vo.request.RegistryCommand;
import com.iohw.knobot.user.domain.vo.request.SendEmailCommand;
import com.iohw.knobot.user.mapper.UserInfoMapper;
import com.iohw.knobot.user.domain.convert.UserInfoConverter;
import com.iohw.knobot.user.domain.vo.response.UserInfoResponse;
import com.iohw.knobot.user.domain.vo.response.UserDetailInfoResp;
import com.iohw.knobot.user.service.UserInfoService;
import com.iohw.knobot.utils.*;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
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
    private final EmailUtil emailUtil;
    private final UserInfoConverter userInfoConverter;
    private final Cache<Object, Object> emailCodeCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    @Override
    public Long createUser(RegistryCommand registryCommand) {
        UserInfoDO user = getUserByUsername(registryCommand.getUsername());
        if(user != null) {
            throw new BusinessException("用户名已存在");
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUsername(registryCommand.getUsername());
        userInfoDO.setPassword(registryCommand.getPassword());
        userInfoDO.setUserId(IdUtil.getSnowflake().nextId());
        userInfoDO.setNickname(NicknameGenerator.generateNickname());
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
    public void updateUserInfo(ModifyUserInfoCommand modifyUserInfoCommand) {
        UserInfoDO userInfo = new UserInfoDO();
        userInfo.setUserId(modifyUserInfoCommand.getUserId());

        MultipartFile avatarFile = modifyUserInfoCommand.getAvatar();
        if(avatarFile != null) {
            try {
                //上传图片到oss
                String originalFilename = avatarFile.getOriginalFilename();
                String type = FileUtils.getTypeByFileName(originalFilename);
                // - 统一为 {userId}.png
                String avatarFileName = modifyUserInfoCommand.getUserId() + "." + type;
                String avatarUrl = OssUtil.upload("avatar", avatarFileName, avatarFile.getInputStream());
                userInfo.setAvatarUrl(avatarUrl);
            } catch (IOException e) {
                throw new BusinessException("图片文件异常");
            }
        }


        userInfo.setPassword(modifyUserInfoCommand.getNewPassword());
        userInfo.setNickname(modifyUserInfoCommand.getNickname());
        userInfo.setDescription(modifyUserInfoCommand.getDescription());
        userInfoMapper.updateById(userInfo);
    }

    @Override
    public boolean deleteUser(Long id) {
        return userInfoMapper.deleteById(id) > 0;
    }

    @Override
    public List<UserInfoDO> listUsers() {
        return userInfoMapper.selectList();
    }

    @Override
    public UserInfoResponse login(HttpServletRequest req, HttpServletResponse resp, LoginCommand request) {
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

        UserInfoResponse dto = userInfoConverter.toDto(user);

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

    @Override
    public UserDetailInfoResp queryUserDetailInfo(QueryUserDetailInfoRequest request) {
        UserInfoDO userInfoDO = userInfoMapper.selectById(request.getUserId());
        if(userInfoDO == null) {
            throw new BusinessException("不存在该用户");
        }
        long joinDays = Duration.between(userInfoDO.getCreateTime(), LocalDateTime.now()).toDays();
        return UserDetailInfoResp.builder()
                .nickName(userInfoDO.getNickname())
                .description(userInfoDO.getDescription())
                .avatarUrl(userInfoDO.getAvatarUrl())
                .joinDays(joinDays)
                .username(userInfoDO.getUsername())
                .email(userInfoDO.getEmail())
                .build();
    }

    @Override
    public void bindEmail(BindEmailCommand bindEmailCommand) {
        String emailCode = (String)emailCodeCache.getIfPresent(bindEmailCommand.getUserId());
        if(emailCode == null || !emailCode.equals(bindEmailCommand.getCode())) {
            throw new BusinessException("邮箱验证码错误或已过期，请重试~");
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUserId(bindEmailCommand.getUserId());
        // 绑定邮箱
        userInfoDO.setEmail(bindEmailCommand.getNewEmail());
        userInfoMapper.updateById(userInfoDO);
    }

    @Override
    public void sendEmail(SendEmailCommand sendEmailCommand) {
        // 1. 缓存验证码
        String code = new Random().nextInt(900000) + 100000 + "";
        emailCodeCache.put(sendEmailCommand.getUserId(), code);
        // 2. 发送验证码邮件
        emailUtil.sendCodeVerifyEmail(sendEmailCommand.getTo(), code);
    }
}
