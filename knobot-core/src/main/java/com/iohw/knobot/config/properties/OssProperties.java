package com.iohw.knobot.config.properties;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @author: iohw
 * @date: 2025/5/6 21:22
 * @description:
 */
@Configuration
@ConfigurationProperties(prefix = "oss")
@Data
public class OssProperties {
    private String bucketName;
    private String endpoint;
    private String accessKeyId;
    private String secretAccessKeyId;
}
