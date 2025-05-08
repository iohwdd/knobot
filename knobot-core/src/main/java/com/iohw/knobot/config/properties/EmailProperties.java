package com.iohw.knobot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "mail")
public class EmailProperties {
    private String fromAddress;
    private String toAddress;
    private String template;
}