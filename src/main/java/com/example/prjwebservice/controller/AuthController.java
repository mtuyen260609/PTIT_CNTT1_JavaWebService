package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.request.LoginRequest;
import com.example.prjwebservice.model.dto.request.RegisterStudentRequest;
import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.AuthResponse;
import com.example.prjwebservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AuthResponse>> login(@Valid @RequestBody LoginRequest request) {
        AuthResponse response = authService.login(request);
        return build(HttpStatus.OK, "Đăng nhập thành công", response);
    }

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AuthResponse>> register(@Valid @RequestBody RegisterStudentRequest request) {
        AuthResponse response = authService.registerStudent(request);
        return build(HttpStatus.CREATED, "Đăng ký sinh viên thành công", response);
    }

    private ResponseEntity<ApiResponse<AuthResponse>> build(HttpStatus status, String message, AuthResponse data) {
        return ResponseEntity.status(status).body(ApiResponse.<AuthResponse>builder()
                .success(status.is2xxSuccessful())
                .message(message)
                .data(data)
                .timestamp(LocalDateTime.now())
                .build());
    }
}
