package com.iohw.knobot.upload;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @author: iohw
 * @date: 2025/4/26 9:57
 * @description:
 */
@Component
public class FileUploadFactory implements ApplicationContextAware, InitializingBean {
    private ApplicationContext applicationContext;
    private final Map<String, UploadFileStrategy> map = new HashMap<>();

    @Value("${upload.way}")
    private String uploadWay;

    public UploadFileStrategy getUploadStrategy() {
        switch (uploadWay) {
            case "local":
                return map.get("localUploadFileStrategy");
            case "oss":
                return map.get("ossUploadFileStrategy");
        }
        return map.get(uploadWay);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @Override
    public void afterPropertiesSet() throws Exception {
        map.putAll(applicationContext.getBeansOfType(UploadFileStrategy.class));
    }
}
