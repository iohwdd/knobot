package com.iohw.knobot.utils;

import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author: iohw
 * @date: 2025/4/26 12:49
 * @description:
 */
public class FileUtils {
    public static double getFileSizeInMB(MultipartFile file) {
        if (file == null) {
            return 0;
        }
        long fileSizeInBytes = file.getSize();
        // 将字节数转换为兆字节，1MB = 1024 * 1024 字节
        return (double) fileSizeInBytes / (1024 * 1024);
    }


    public static void deleteFile(String filePath) {
        File file = new File(filePath);
        if (file.exists()) {
            file.delete();
        }
    }
}
