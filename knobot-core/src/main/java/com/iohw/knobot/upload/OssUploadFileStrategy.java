package com.iohw.knobot.upload;

import com.iohw.knobot.common.dto.FileUploadDTO;
import com.iohw.knobot.common.exception.FileUploadException;
import com.iohw.knobot.utils.OssUtil;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.UUID;

/**
 * @author: iohw
 * @date: 2025/5/6 21:50
 * @description:
 */
@Component
public class OssUploadFileStrategy implements UploadFileStrategy {
    @Override
    public FileUploadDTO upload(MultipartFile file, String path) {
        try {
            //String name = file.getOriginalFilename();
            String name = UUID.randomUUID().toString();
            InputStream in = file.getInputStream();
            String url = OssUtil.upload(path, name, in);
            return FileUploadDTO.builder()
                    .fileId(String.valueOf(System.currentTimeMillis()))
                    .fileName(name)
                    .filePath(path)
                    .fileUrl(url)
                    .build();
        } catch (IOException e) {
            throw new FileUploadException("文件上传失败");
        }
    }
}
