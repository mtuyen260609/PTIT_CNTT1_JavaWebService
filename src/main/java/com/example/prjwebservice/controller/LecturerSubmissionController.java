package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.request.GradeRequest;
import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.SubmissionResponse;
import com.example.prjwebservice.service.SubmissionService;
import com.example.prjwebservice.repository.SubmissionRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/lecturer")
@RequiredArgsConstructor
public class LecturerSubmissionController {

    private final SubmissionService submissionService;
    private final SubmissionRepository submissionRepository;

    @PostMapping("/grades")
    public ResponseEntity<ApiResponse<SubmissionResponse>> gradeSubmission(@Valid @RequestBody GradeRequest request) {
        SubmissionResponse response = submissionService.grade(request);
        return ResponseEntity.ok(ApiResponse.<SubmissionResponse>builder()
                .success(true)
                .message("Chấm điểm thành công")
                .data(response)
                .timestamp(LocalDateTime.now())
                .build());
    }

    @GetMapping("/courses/{courseId}/submissions")
    public ResponseEntity<ApiResponse<List<SubmissionResponse>>> getSubmissions(@PathVariable Long courseId) {
        List<SubmissionResponse> list = submissionService.findByCourse(courseId);

        return ResponseEntity.ok(ApiResponse.<List<SubmissionResponse>>builder()
                .success(true)
                .message("Lấy danh sách bài nộp thành công")
                .data(list)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
