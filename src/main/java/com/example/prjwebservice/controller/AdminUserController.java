package com.example.prjwebservice.controller;

import com.example.prjwebservice.model.dto.request.UserUpsertRequest;
import com.example.prjwebservice.model.dto.response.ApiResponse;
import com.example.prjwebservice.model.dto.response.UserResponse;
import com.example.prjwebservice.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/admin/users")
@RequiredArgsConstructor
public class AdminUserController {

    private final UserService userService;

    @GetMapping
    public ResponseEntity<ApiResponse<Page<UserResponse>>> search(
            @RequestParam(required = false) String keyword,
            Pageable pageable
    ) {
        return build(HttpStatus.OK, "Lấy danh sách người dùng thành công", userService.search(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> getById(@PathVariable Long id) {
        return build(HttpStatus.OK, "Lấy chi tiết người dùng thành công", userService.getById(id));
    }

    @PostMapping
    public ResponseEntity<ApiResponse<UserResponse>> create(@Valid @RequestBody UserUpsertRequest request) {
        return build(HttpStatus.CREATED, "Tạo người dùng thành công", userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ApiResponse<UserResponse>> update(
            @PathVariable Long id,
            @Valid @RequestBody UserUpsertRequest request
    ) {
        return build(HttpStatus.OK, "Cập nhật người dùng thành công", userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<Void>> delete(@PathVariable Long id) {
        userService.delete(id);
        return build(HttpStatus.NO_CONTENT, "Xóa người dùng thành công", null);
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
