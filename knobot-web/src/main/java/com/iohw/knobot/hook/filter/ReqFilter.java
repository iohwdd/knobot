package com.iohw.knobot.hook.filter;

import com.iohw.knobot.common.constant.Constants;
import com.iohw.knobot.common.dto.TokenPayloadDTO;
import com.iohw.knobot.utils.CookieUtils;
import com.iohw.knobot.utils.JwtUtils;
import com.iohw.knobot.utils.SelfTraceIdGenerator;
import com.iohw.knobot.utils.ThreadLocalUtils;

import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.MDC;

import java.io.IOException;

/**
 * @author: iohw
 * @date: 2025/5/6 20:32
 * @description:
 */
public class ReqFilter implements Filter {
    private final String TRACE_ID = "traceId";
    @Override
    public void doFilter(ServletRequest req, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // 设置请求链路唯一标识 - traceId
            MDC.put(TRACE_ID, SelfTraceIdGenerator.generate());

            filterChain.doFilter(req, servletResponse);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}
