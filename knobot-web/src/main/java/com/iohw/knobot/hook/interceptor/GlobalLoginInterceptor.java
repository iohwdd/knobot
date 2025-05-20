package com.iohw.knobot.hook.interceptor;

import com.iohw.knobot.common.ReqContext;
import com.iohw.knobot.common.constant.Constants;
import com.iohw.knobot.common.dto.TokenPayloadDTO;
import com.iohw.knobot.common.exception.BusinessException;
import com.iohw.knobot.user.domain.entity.UserInfoDO;
import com.iohw.knobot.user.service.IUserInfoService;
import com.iohw.knobot.utils.CookieUtils;
import com.iohw.knobot.utils.JwtUtils;
import com.iohw.knobot.utils.ThreadLocalUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.iohw.knobot.common.constant.Constants.REQ_CONTEXT;
import static com.iohw.knobot.user.service.IUserInfoService.TOKEN;

/**
 * @author: iohw
 * @date: 2025/5/4 22:00
 * @description: 全局登录拦截器
 */
@Component
@RequiredArgsConstructor
public class GlobalLoginInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String token = CookieUtils.findValueByName(Constants.ACCESS_TOKEN_COOKIE_NAME, request);

        TokenPayloadDTO tokenPayloadDTO = JwtUtils.parseToken(token);
        if(tokenPayloadDTO == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);

            return false;
        }

        // 设置用户上下文信息
        ThreadLocalUtils.set(Constants.REQ_CONTEXT, tokenPayloadDTO);

        return true;
    }

}