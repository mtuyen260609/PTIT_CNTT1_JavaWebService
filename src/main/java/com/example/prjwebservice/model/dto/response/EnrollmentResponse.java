package com.example.prjwebservice.model.dto.response;

import com.example.prjwebservice.model.entity.EnrollmentStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class EnrollmentResponse {

    private final Long id;
    private final Long studentId;
    private final String studentName;
    private final Long courseId;
    private final String courseName;
    private final EnrollmentStatus status;
    private final LocalDateTime enrolledAt;
}
