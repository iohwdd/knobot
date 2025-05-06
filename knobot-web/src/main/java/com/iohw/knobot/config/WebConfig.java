package com.iohw.knobot.config;

import com.iohw.knobot.hook.filter.ReqFilter;
import com.iohw.knobot.hook.interceptor.DocumentUploadInterceptor;
import com.iohw.knobot.hook.interceptor.GlobalLoginInterceptor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

/**
 * @author: iohw
 * @date: 2025/5/4 22:00
 * @description: Web配置类
 */
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final GlobalLoginInterceptor globalLoginInterceptor;
    private final DocumentUploadInterceptor documentUploadInterceptor;

    private final List<String> uploadDocExcludeUrls = List.of(
            "/chat/upload",
            "/chat/messages",
            "/chat/conversation-history",
            "/chat/conversation-create",
            "/chat/conversation-delete",
            "/chat/conversation-title-update"
    );

    private final List<String> loginExcludeUrls = List.of(
            "/user/login",
            "/user/registry",
            "/user/logout",
            "/error",
            "/swagger-ui/**",
            "/v3/api-docs/**"
    );

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        // 文档上传拦截器
        registry.addInterceptor(documentUploadInterceptor)
                .addPathPatterns("/chat/**")
                .addPathPatterns("/library/createKnowledgeLibDocument")
                .excludePathPatterns(uploadDocExcludeUrls);

        // 全局登录拦截器
//        registry.addInterceptor(globalLoginInterceptor)
//                .addPathPatterns("/**")
//                .excludePathPatterns(loginExcludeUrls);
    }

    @Bean
    public FilterRegistrationBean<ReqFilter> reqFilter() {
        FilterRegistrationBean<ReqFilter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter(new ReqFilter());
        filterRegistrationBean.addUrlPatterns("/*");
        filterRegistrationBean.setOrder(1);
        return filterRegistrationBean;
    }
}