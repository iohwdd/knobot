package com.iohw.knobot.utils;

import com.aliyun.oss.OSS;
import com.aliyun.oss.OSSClientBuilder;
import com.aliyun.oss.model.PutObjectRequest;
import com.aliyun.oss.model.PutObjectResult;
import com.iohw.knobot.config.properties.ConfigHolder;
import com.iohw.knobot.config.properties.OssProperties;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * @author: iohw
 * @date: 2025/5/6 21:18
 * @description:
 */

public class OssUtil {
    private static final OssProperties properties = ConfigHolder.ossProperties;

    /**
     *
     * @param path
     * @param fileName
     * @param in
     * @return url
     */
    public static String upload(String path, String fileName, InputStream in) {
        OSS ossClient = new OSSClientBuilder().build(properties.getEndpoint(), properties.getAccessKeyId(), properties.getSecretAccessKeyId());
        String name = path + "/" + fileName;
        PutObjectRequest putObjectRequest = new PutObjectRequest(properties.getBucketName(), name, in);
        String url = "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/" + fileName;
        ossClient.putObject(putObjectRequest);
        return url;
    }
}
