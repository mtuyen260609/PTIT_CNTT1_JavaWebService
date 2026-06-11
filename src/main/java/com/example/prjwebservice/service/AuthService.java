package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.request.LoginRequest;
import com.example.prjwebservice.model.dto.request.RegisterStudentRequest;
import com.example.prjwebservice.model.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse registerStudent(RegisterStudentRequest request);
}
