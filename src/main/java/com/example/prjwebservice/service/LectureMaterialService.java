package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.response.LectureMaterialResponse;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface LectureMaterialService {
    LectureMaterialResponse upload(Long courseId, String title, MultipartFile file);
    List<LectureMaterialResponse> getByCourse(Long courseId);
}
