package com.iohw.knobot.upload;

import com.iohw.knobot.upload.dto.FileUploadDTO;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

/**
 * @author: iohw
 * @date: 2025/4/26 10:03
 * @description:
 */
@Component
public class LocalUploadFileStrategy implements UploadFileStrategy {
    private String baseDir = "./files";

    @Override
    public FileUploadDTO upload(MultipartFile file, String path) {
        try {
            Path uploadPath = Paths.get(baseDir + path);
            if(!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }
            String fileType = file.getOriginalFilename().substring(file.getOriginalFilename().lastIndexOf("."));
            String fileName = new StringBuilder(UUID.randomUUID().toString()).append(fileType).toString();

            Path filePath = uploadPath.resolve(fileName);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            return FileUploadDTO.builder()
                    .fileName(fileName)
                    .filePath(String.valueOf(filePath))
                    .fileId(String.valueOf(System.currentTimeMillis()))
                    .build();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
