package com.iohw.knobot.interceptor;

import com.iohw.knobot.common.exception.BusinessException;
import com.iohw.knobot.utils.ThreadLocalUtils;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * @author: iohw
 * @date: 2025/4/30 22:52
 * @description:
 */
@Component
public class DocumentUploadInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        String uri = request.getRequestURL().toString();
        String memoryId = uri.substring(uri.lastIndexOf("/") + 1);
        String knowledgeLibId = request.getParameter("knowledgeLibId");

        if(StringUtils.hasText(memoryId))
            ThreadLocalUtils.set("memoryId", memoryId);
        if(StringUtils.hasText(knowledgeLibId))
            ThreadLocalUtils.set("knowledgeLibId", knowledgeLibId);

        return true;
    }
}
