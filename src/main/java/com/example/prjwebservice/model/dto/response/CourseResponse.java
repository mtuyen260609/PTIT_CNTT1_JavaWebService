package com.example.prjwebservice.model.dto.response;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CourseResponse {

    private final Long id;
    private final String courseCode;
    private final String courseName;
    private final String description;
    private final Integer credits;
    private final boolean active;
    private final Long lecturerId;
    private final String lecturerName;
}
