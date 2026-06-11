package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.EnrollmentResponse;
import com.example.prjwebservice.service.EnrollmentService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/student")
@RequiredArgsConstructor
public class StudentEnrollmentController {

    private final EnrollmentService enrollmentService;

    @PostMapping("/courses/{courseId}/register")
    public ResponseEntity<ApiResponse<EnrollmentResponse>> enroll(@PathVariable @NotNull Long courseId) {
        return build(HttpStatus.CREATED, "Đăng ký khóa học thành công", enrollmentService.enrollCurrentStudent(courseId));
    }

    @GetMapping("/enrollments")
    public ResponseEntity<ApiResponse<List<EnrollmentResponse>>> myEnrollments() {
        return build(HttpStatus.OK, "Lấy danh sách đăng ký thành công", enrollmentService.getMyEnrollments());
    }

    private <T> ResponseEntity<ApiResponse<T>> build(HttpStatus status, String message, T data) {
        return ResponseEntity.status(status).body(ApiResponse.<T>builder()
                .success(status.is2xxSuccessful())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
