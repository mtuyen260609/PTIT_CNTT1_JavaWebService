package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.request.CourseUpsertRequest;
import com.example.prjwebservice.model.dto.response.CourseResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface CourseService {

    Page<CourseResponse> search(String keyword, Pageable pageable);

    CourseResponse getById(Long id);

    CourseResponse create(CourseUpsertRequest request);

    CourseResponse update(Long id, CourseUpsertRequest request);

    void deactivate(Long id);
}
