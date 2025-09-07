package com.iohw.knobot.user.service.impl;

import cn.hutool.core.util.IdUtil;

import com.alibaba.fastjson.JSON;
import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.iohw.knobot.common.ReqContext;
import com.iohw.knobot.common.constant.Constants;
import com.iohw.knobot.common.dto.TokenPayloadDTO;
import com.iohw.knobot.common.enums.ErrorEnum;
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
import com.iohw.knobot.user.service.IUserInfoService;
import com.iohw.knobot.utils.*;

import cn.hutool.crypto.digest.BCrypt;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.TimeUnit;

import static com.iohw.knobot.common.constant.Constants.REQ_CONTEXT;
import static net.sf.jsqlparser.util.validation.metadata.NamedObject.user;

/**
 * @author: iohw
 * @date: 2025/5/4 22:27
 * @description: 用户信息服务实现类
 */
@Service
@RequiredArgsConstructor
public class UserInfoServiceImpl implements IUserInfoService {
    private final UserInfoMapper userInfoMapper;
    private final StringRedisTemplate stringRedisTemplate;
    private final EmailUtil emailUtil;
    private final UserInfoConverter userInfoConverter;
    private final Cache<Object, Object> emailCodeCache = Caffeine.newBuilder()
            .maximumSize(100)
            .expireAfterWrite(60, TimeUnit.SECONDS)
            .build();

    @Override
    public UserInfoResponse login(HttpServletRequest req, HttpServletResponse resp, LoginCommand request) {
        UserInfoDO user = userInfoMapper.selectByUsername(request.getUsername());
        // 账号或密码错误
        if (user == null || !BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            return null;
        }


        getAccessTokenAndRefresh(resp, TokenPayloadDTO.builder()
            .username(user.getUsername())
            .userId(user.getUserId())
            .avatarUrl(user.getAvatarUrl())
            .build());

        // UserInfoResponse dto = userInfoConverter.toDto(user);

        return UserInfoResponse.builder()
            .userId(user.getUserId())
            .userName(user.getUsername())
            .nickName(user.getNickname())
            .avatarUrl(user.getAvatarUrl())
            .build();
    }

    @Override
    public String refresh(HttpServletRequest req, HttpServletResponse resp, String refreshToken) {
        Boolean flag = stringRedisTemplate.hasKey(refreshToken);

        if (Boolean.FALSE.equals(flag)) {
            throw new BusinessException(ErrorEnum.REFRESH_TOKEN_INVALID.getDesc());
        }

        // 校验refreshToken是否有效
        String json = stringRedisTemplate.opsForValue()
            .get(refreshToken);
        TokenPayloadDTO requestInfo = JSON.parseObject(json, TokenPayloadDTO.class);
        if(refreshToken == null) {
            throw new BusinessException(ErrorEnum.REFRESH_TOKEN_INVALID.getDesc());
        }

        // 删除旧的refreshToken
        stringRedisTemplate.delete(refreshToken);
        CookieUtils.deleteCookieByName(req, resp, Constants.REFRESH_TOKEN_COOKIE_NAME);

        return getAccessTokenAndRefresh(resp, requestInfo);
    }

    private String getAccessTokenAndRefresh(HttpServletResponse resp, TokenPayloadDTO requestInfo) {
        if(requestInfo == null) {
            throw new BusinessException(ErrorEnum.REFRESH_TOKEN_INVALID.getDesc());
        }
        // 生成accessToken与refreshToken
        String accessToken = JwtUtils.generateAccessToken(requestInfo);
        String refreshTokenKey = UUID.randomUUID().toString();

        // redis/cookie 保存 refreshToken
        stringRedisTemplate.opsForValue().set(refreshTokenKey, JSON.toJSONString(requestInfo), Duration.ofMillis(Constants.REFRESH_TOKEN_REDIS_EXPIRE_TIME));
        CookieUtils.addCookie(resp, Constants.REFRESH_TOKEN_COOKIE_NAME, refreshTokenKey, Constants.REFRESH_TOKEN_EXPIRE_TIME);
        // cookie保存accessToken
        CookieUtils.addCookie(resp, Constants.ACCESS_TOKEN_COOKIE_NAME, accessToken, Constants.ACCESS_TOKEN_EXPIRE_TIME);


        return accessToken;
    }

    @Override
    public void logout(HttpServletResponse resp, HttpServletRequest req) {
        stringRedisTemplate.delete(Constants.REFRESH_TOKEN_COOKIE_NAME);

        CookieUtils.deleteCookieByName(req, resp, Constants.REFRESH_TOKEN_COOKIE_NAME);
        CookieUtils.deleteCookieByName(req, resp, Constants.ACCESS_TOKEN_COOKIE_NAME);

        ThreadLocalUtils.remove(REQ_CONTEXT);
    }

    @Override
    public Long registry(RegistryCommand registryCommand) {
        UserInfoDO user = getUserByUsername(registryCommand.getUsername());
        if(user != null) {
            throw new BusinessException("用户名已存在");
        }
        UserInfoDO userInfoDO = new UserInfoDO();
        userInfoDO.setUsername(registryCommand.getUsername());
        userInfoDO.setPassword(BCrypt.hashpw(registryCommand.getPassword(), BCrypt.gensalt()));
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

        if(StringUtils.hasText(modifyUserInfoCommand.getNewPassword()))
            userInfo.setPassword(BCrypt.hashpw(modifyUserInfoCommand.getNewPassword(), BCrypt.gensalt()));
        if(StringUtils.hasText(modifyUserInfoCommand.getNickname()))
            userInfo.setNickname(modifyUserInfoCommand.getNickname());
        if(StringUtils.hasText(modifyUserInfoCommand.getDescription()))
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
    public UserDetailInfoResp queryUserDetailInfo(QueryUserDetailInfoRequest request) {
        UserInfoDO userInfoDO = userInfoMapper.selectById(request.getUserId());
        if(userInfoDO == null) {
            throw new BusinessException("不存在该用户");
        }
        long joinDays = Duration.between(userInfoDO.getCreateTime(), LocalDateTime.now()).toDays();
        return UserDetailInfoResp.builder()
                .description(userInfoDO.getDescription())
                .nickname(userInfoDO.getNickname())
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
