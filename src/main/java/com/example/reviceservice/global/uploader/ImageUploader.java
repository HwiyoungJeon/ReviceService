package com.example.reviceservice.global.uploader;

import com.example.reviceservice.global.exception.ReviewException;
import com.example.reviceservice.global.message.GlobalMessage;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

@Component
public class ImageUploader {

    public String upload(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new ReviewException(GlobalMessage.NOT_FOUND_IMAGE);
        }

        // 실제 S3 업로드 로직 대신 더미 URL 반환
        return file.getOriginalFilename();
    }
}