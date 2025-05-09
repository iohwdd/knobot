package com.iohw.knobot.hook.interceptor;

import com.iohw.knobot.common.ReqContext;
import com.iohw.knobot.user.domain.entity.UserInfoDO;
import com.iohw.knobot.user.service.UserInfoService;
import com.iohw.knobot.utils.ThreadLocalUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import static com.iohw.knobot.common.constant.Constants.REQ_CONTEXT;
import static com.iohw.knobot.user.service.UserInfoService.TOKEN;

/**
 * @author: iohw
 * @date: 2025/5/4 22:00
 * @description: 全局登录拦截器
 */
@Component
@RequiredArgsConstructor
public class GlobalLoginInterceptor implements HandlerInterceptor {
    private final StringRedisTemplate stringRedisTemplate;
    private final UserInfoService userInfoService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Cookie[] cookies = request.getCookies();
        if (cookies == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String token = null;
        for (Cookie cookie : cookies) {
            if (TOKEN.equals(cookie.getName())) {
                token = cookie.getValue();
                break;
            }
        }

        if (token == null || !stringRedisTemplate.hasKey(token)) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        String userId = stringRedisTemplate.opsForValue().get(token);
        if (userId == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        UserInfoDO user = userInfoService.getUserById(Long.parseLong(userId));
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            return false;
        }

        // 设置上下文用户信息
        ReqContext reqContext = ReqContext.builder()
                .userId(user.getUserId())
                .userName(user.getUsername())
                .nickName(user.getNickname())
                .avatarUrl(user.getAvatarUrl())
                .build();
        ThreadLocalUtils.set(REQ_CONTEXT, reqContext);

        return true;
    }

}