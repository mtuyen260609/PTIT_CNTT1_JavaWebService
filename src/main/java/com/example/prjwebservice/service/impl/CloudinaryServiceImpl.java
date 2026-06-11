package com.example.prjwebservice.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.example.prjwebservice.service.CloudinaryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;

@Service
public class CloudinaryServiceImpl implements CloudinaryService {

    @Value("${cloudinary.url}")
    private String cloudinaryUrl;

    @Override
    public String uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("File không được trống");
        }
        
        // Cố gắng parse URL. Nếu là dummy thì mock kết quả.
        if (cloudinaryUrl.contains("my_key")) {
            // Mock upload
            return "https://mock-cloud-storage.com/dummy-file-url-" + System.currentTimeMillis() + ".pdf";
        }

        try {
            Cloudinary cloudinary = new Cloudinary(cloudinaryUrl);
            Map params = ObjectUtils.asMap("resource_type", "auto");
            Map uploadResult = cloudinary.uploader().upload(file.getBytes(), params);
            return uploadResult.get("secure_url").toString();
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tải file lên Cloudinary", e);
        }
    }
}
