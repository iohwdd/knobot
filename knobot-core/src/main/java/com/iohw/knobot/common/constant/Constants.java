package com.iohw.knobot.common.constant;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * @author: iohw
 * @date: 2025/5/4 22:44
 * @description:
 */
public class Constants {
    public static final String REQ_CONTEXT = "REQ_CONTEXT";
    // refresh_token cookie 名称
    public static final String REFRESH_TOKEN_COOKIE_NAME = "refresh_token";
    // access_token cookie 名称
    public static final String ACCESS_TOKEN_COOKIE_NAME = "access_token";
    //  refresh_token 过期时间 七天
    public static final int REFRESH_TOKEN_EXPIRE_TIME = 60 * 60 * 24 * 7 * 1000;
    //  access_token 过期时间 1小时
    public static final int ACCESS_TOKEN_EXPIRE_TIME = 60 * 60 * 1000;
    // 用于给Jwt令牌签名校验的秘钥
    public static final String JWT_SIGN_KEY = "1145141919810";
    public static final SecretKey KEY = new SecretKeySpec(
        Arrays.copyOf(JWT_SIGN_KEY.getBytes(StandardCharsets.UTF_8), 64), "HmacSHA256");
}
