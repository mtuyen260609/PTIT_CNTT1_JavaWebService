package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.request.LoginRequest;
import com.example.prjwebservice.model.dto.request.RegisterStudentRequest;
import com.example.prjwebservice.model.dto.request.TokenRefreshRequest;
import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.AuthResponse;
import com.example.prjwebservice.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/auth")
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

    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AuthResponse>> refresh(@Valid @RequestBody TokenRefreshRequest request) {
        AuthResponse response = authService.refresh(request);
        return build(HttpStatus.OK, "Làm mới token thành công", response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(@RequestHeader(value = "Authorization", required = false) String token) {
        authService.logout(token);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Đăng xuất thành công")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/change-password")
    public ResponseEntity<ApiResponse<Void>> changePassword(@Valid @RequestBody com.example.prjwebservice.model.dto.request.ChangePasswordRequest request) {
        authService.changePassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Đổi mật khẩu thành công")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<ApiResponse<Void>> forgotPassword(@Valid @RequestBody com.example.prjwebservice.model.dto.request.ForgotPasswordRequest request) {
        authService.forgotPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Yêu cầu reset mật khẩu đã được gửi (Check console để lấy token)")
                .timestamp(LocalDateTime.now())
                .build());
    }

    @PostMapping("/reset-password")
    public ResponseEntity<ApiResponse<Void>> resetPassword(@Valid @RequestBody com.example.prjwebservice.model.dto.request.ResetPasswordRequest request) {
        authService.resetPassword(request);
        return ResponseEntity.ok(ApiResponse.<Void>builder()
                .success(true)
                .message("Reset mật khẩu thành công")
                .timestamp(LocalDateTime.now())
                .build());
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
