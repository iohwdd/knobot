package com.iohw.knobot.hook.filter;

import com.iohw.knobot.utils.SelfTraceIdGenerator;
import jakarta.servlet.*;
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
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        try {
            // 设置请求链路唯一标识 - traceId
            MDC.put(TRACE_ID, SelfTraceIdGenerator.generate());
            filterChain.doFilter(servletRequest, servletResponse);
        } finally {
            MDC.remove(TRACE_ID);
        }
    }
}
