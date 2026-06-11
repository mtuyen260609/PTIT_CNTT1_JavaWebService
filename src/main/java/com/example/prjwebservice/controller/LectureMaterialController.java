package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.LectureMaterialResponse;
import com.example.prjwebservice.service.LectureMaterialService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
public class LectureMaterialController {

    private final LectureMaterialService lectureMaterialService;

    @PostMapping("/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<LectureMaterialResponse>> uploadMaterial(
            @PathVariable Long courseId,
            @RequestParam("title") String title,
            @RequestParam("file") MultipartFile file) {

        LectureMaterialResponse response = lectureMaterialService.upload(courseId, title, file);
        return ResponseEntity.ok(ApiResponse.<LectureMaterialResponse>builder()
                .success(true)
                .message("Tải tài liệu lên thành công")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/courses/{courseId}/materials")
    public ResponseEntity<ApiResponse<List<LectureMaterialResponse>>> getMaterials(@PathVariable Long courseId) {
        List<LectureMaterialResponse> list = lectureMaterialService.getByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.<List<LectureMaterialResponse>>builder()
                .success(true)
                .message("Lấy danh sách tài liệu thành công")
                .data(list)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
