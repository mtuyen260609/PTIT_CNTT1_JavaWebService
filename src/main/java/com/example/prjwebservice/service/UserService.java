package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.request.UserUpsertRequest;
import com.example.prjwebservice.model.dto.response.UserResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    Page<UserResponse> search(String keyword, Pageable pageable);

    UserResponse getById(Long id);

    UserResponse create(UserUpsertRequest request);

    UserResponse update(Long id, UserUpsertRequest request);

    void deactivate(Long id);
}
