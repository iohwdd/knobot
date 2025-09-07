package com.iohw.knobot.utils;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutionException;

import org.springframework.util.StringUtils;

import com.iohw.knobot.common.constant.Constants;
import com.iohw.knobot.common.dto.TokenPayloadDTO;
import com.iohw.knobot.common.enums.ErrorEnum;
import com.iohw.knobot.common.exception.UnAuthorizedException;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.extern.slf4j.Slf4j;

/**
 * @author: iohw
 * @date: 2025/5/20 15:44
 * @description:
 */
@Slf4j
public class JwtUtils {

    /**
     * 生成accessToken
     *
     * @param payload
     * @return
     */
    public static String generateAccessToken(TokenPayloadDTO payload) {
        if (payload == null) {
            return null;
        }

        Map<String, Object> claims = Map.of(
            "userId", payload.getUserId(),
            "avatarUrl", payload.getAvatarUrl(),
            "username", payload.getUsername()
        );
        return Jwts.builder()
            .addClaims(claims)
            .setIssuedAt(new Date())
            .setIssuer("knobot")
            .setExpiration(new Date(System.currentTimeMillis() + Constants.JWT_ACCESS_TOKEN_EXPIRE_TIME))
            .signWith(SignatureAlgorithm.HS256, Constants.KEY)
            .compact();
    }

    /**
     * 解析 JWT 获取用户基础信息
     * @param token
     * @return
     */
    public static TokenPayloadDTO parseToken(String token) {
        if(StringUtils.hasText(token)) {
            try {
                Claims claims = Jwts.parser()
                    .setSigningKey(Constants.KEY)
                    .parseClaimsJws(token)
                    .getBody();
                return TokenPayloadDTO.builder()
                    .userId(claims.get("userId", Long.class))
                    .avatarUrl(claims.get("avatarUrl", String.class))
                    .username(claims.get("userName", String.class))
                    .build();
            }catch (Exception e) {
                log.warn("JWT token 解析失败 ", e);
                throw new UnAuthorizedException(ErrorEnum.ACCESS_TOKEN_INVALID);
            }
        }
        return null;
    }

}
