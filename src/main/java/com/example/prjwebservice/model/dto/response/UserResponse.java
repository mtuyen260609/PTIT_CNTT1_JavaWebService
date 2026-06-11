package com.example.prjwebservice.model.dto.response;

import com.example.prjwebservice.model.entity.Role;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class UserResponse {

    private final Long id;
    private final String username;
    private final String fullName;
    private final String email;
    private final String role;
    private final boolean active;
}
