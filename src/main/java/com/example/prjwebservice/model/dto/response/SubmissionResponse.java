package com.example.prjwebservice.model.dto.response;

import com.example.prjwebservice.model.entity.SubmissionStatus;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class SubmissionResponse {
    private final Long id;
    private final String reportUrl;
    private final Integer score;
    private final String feedback;
    private final SubmissionStatus status;
    private final Long studentId;
    private final String studentName;
    private final Long courseId;
    private final String courseName;
    private final Long lecturerId;
    private final String lecturerName;
    private final LocalDateTime submittedAt;
}
