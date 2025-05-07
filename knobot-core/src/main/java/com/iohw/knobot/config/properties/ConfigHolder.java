package com.iohw.knobot.config.properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ConfigHolder {
    public static OssProperties ossProperties;

    @Autowired
    public void setConfig(OssProperties ossProperties) {
        ConfigHolder.ossProperties = ossProperties;
    }
}
