package com.iohw.knobot.chat.domain.vo.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class FileUploadResponse {
    private String fileId;   // 文件ID
    private String fileUrl;  // 文件URL（如果需要）
    private String filePath;
    private String fileName; // 文件名
}