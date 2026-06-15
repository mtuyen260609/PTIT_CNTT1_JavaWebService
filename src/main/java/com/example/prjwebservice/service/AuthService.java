package com.example.prjwebservice.service;

import com.example.prjwebservice.model.dto.request.ChangePasswordRequest;
import com.example.prjwebservice.model.dto.request.LoginRequest;
import com.example.prjwebservice.model.dto.request.RegisterStudentRequest;
import com.example.prjwebservice.model.dto.request.TokenRefreshRequest;
import com.example.prjwebservice.model.dto.response.AuthResponse;

public interface AuthService {

    AuthResponse login(LoginRequest request);

    AuthResponse registerStudent(RegisterStudentRequest request);

    AuthResponse refresh(TokenRefreshRequest request);

    void logout(String accessToken, com.example.prjwebservice.model.dto.request.LogoutRequest request);

    void changePassword(ChangePasswordRequest request);

    void forgotPassword(com.example.prjwebservice.model.dto.request.ForgotPasswordRequest request);

    void resetPassword(com.example.prjwebservice.model.dto.request.ResetPasswordRequest request);
}
