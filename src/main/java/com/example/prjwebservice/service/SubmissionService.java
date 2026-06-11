package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.request.GradeRequest;
import com.example.prjwebservice.model.dto.response.SubmissionResponse;
import org.springframework.web.multipart.MultipartFile;

public interface SubmissionService {
    SubmissionResponse submit(Long courseId, MultipartFile file);

    SubmissionResponse grade(GradeRequest request);

    java.util.List<SubmissionResponse> findByCourse(Long courseId);
}
