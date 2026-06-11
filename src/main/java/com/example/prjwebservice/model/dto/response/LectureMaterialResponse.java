package com.example.prjwebservice.model.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
public class LectureMaterialResponse {
    private final Long id;
    private final String title;
    private final String fileUrl;
    private final Long courseId;
    private final Long lecturerId;
    private final LocalDateTime uploadedAt;
}
