package com.iohw.knobot.upload;

import com.iohw.knobot.upload.dto.FileUploadDTO;
import org.springframework.web.multipart.MultipartFile;

/**
 * @author: iohw
 * @date: 2025/4/26 10:03
 * @description:
 */
public interface UploadFileStrategy {
    FileUploadDTO upload(MultipartFile file, String path);
}
