package com.example.prjwebservice.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class ApiResponse<T> {

    private final boolean success;
    private final String message;
    private final T data;
    private final java.util.List<String> errors;
    private final LocalDateTime timestamp;
}
