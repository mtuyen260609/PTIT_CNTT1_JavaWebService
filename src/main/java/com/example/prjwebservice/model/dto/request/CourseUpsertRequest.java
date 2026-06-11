package com.example.prjwebservice.model.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CourseUpsertRequest {

    @NotBlank
    @Size(max = 50)
    private String courseCode;

    @NotBlank
    @Size(max = 150)
    private String courseName;

    @Size(max = 1000)
    private String description;

    @NotNull
    @Positive
    private Integer credits;

    private Boolean active;

    private Long lecturerId;
}
