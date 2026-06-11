package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.SubmissionResponse;
import com.example.prjwebservice.service.SubmissionService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentSubmissionController {

    private final SubmissionService submissionService;

    @PostMapping("/submissions/{courseId}/upload")
    public ResponseEntity<ApiResponse<SubmissionResponse>> submitAssignment(
            @PathVariable Long courseId,
            @RequestParam("file") MultipartFile file) {
        SubmissionResponse response = submissionService.submit(courseId, file);
        return ResponseEntity.ok(ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Nộp bài báo cáo thành công")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
