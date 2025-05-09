package com.iohw.knobot.upload.dto;

import lombok.Builder;
import lombok.Data;

/**
 * @author: iohw
 * @date: 2025/4/26 10:45
 * @description:
 */
@Data
@Builder
public class FileUploadDTO {
    private String fileId;   // 文件ID
    private String fileUrl;  // 文件URL（如果需要）
    private String filePath;
    private String fileName; // 文件名
}
