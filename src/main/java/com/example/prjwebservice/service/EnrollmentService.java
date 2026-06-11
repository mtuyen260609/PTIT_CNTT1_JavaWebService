package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.response.EnrollmentResponse;

import java.util.List;

public interface EnrollmentService {

    EnrollmentResponse enrollCurrentStudent(Long courseId);

    List<EnrollmentResponse> getMyEnrollments();
}
