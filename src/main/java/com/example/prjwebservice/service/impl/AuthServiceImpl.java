package com.example.prjwebservice.service.impl;

import com.example.prjwebservice.exception.BadRequestException;
import com.example.prjwebservice.exception.ConflictException;
import com.example.prjwebservice.exception.ResourceNotFoundException;
import com.example.prjwebservice.model.dto.request.LoginRequest;
import com.example.prjwebservice.model.dto.request.RegisterStudentRequest;
import com.example.prjwebservice.model.dto.request.TokenRefreshRequest;
import com.example.prjwebservice.model.dto.response.AuthResponse;
import com.example.prjwebservice.model.dto.response.UserResponse;
import com.example.prjwebservice.model.entity.Role;
import com.example.prjwebservice.model.entity.TokenBlacklist;
import com.example.prjwebservice.model.entity.User;
import com.example.prjwebservice.repository.RoleRepository;
import com.example.prjwebservice.repository.TokenBlacklistRepository;
import com.example.prjwebservice.repository.UserRepository;
import com.example.prjwebservice.security.jwt.JwtService;
import com.example.prjwebservice.service.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final TokenBlacklistRepository tokenBlacklistRepository;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @Override
    public AuthResponse login(LoginRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse registerStudent(RegisterStudentRequest request) {
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new ConflictException("Username đã tồn tại");
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new ConflictException("Email đã tồn tại");
        }

        com.example.prjwebservice.model.entity.Role role = roleRepository.findByName("STUDENT")
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy quyền STUDENT"));

        User user = User.builder()
                .username(request.getUsername())
                .password(passwordEncoder.encode(request.getPassword()))
                .fullName(request.getFullName())
                .email(request.getEmail())
                .role(role)
                .deleted(false)
                .createdAt(LocalDateTime.now())
                .build();

        user = userRepository.save(user);

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public AuthResponse refresh(TokenRefreshRequest request) {
        String refreshToken = request.getRefreshToken();
        
        // Kiểm tra blacklist
        if (tokenBlacklistRepository.existsByTokenString(refreshToken)) {
            throw new BadRequestException("Refresh token đã bị vô hiệu hóa");
        }

        String username;
        try {
            username = jwtService.extractUsername(refreshToken);
        } catch (Exception e) {
            throw new BadRequestException("Refresh token không hợp lệ");
        }

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!jwtService.isTokenValid(refreshToken, user)) {
            throw new BadRequestException("Refresh token không hợp lệ hoặc đã hết hạn");
        }

        // Token Rotation: Vô hiệu hóa refresh token cũ ngay sau khi dùng để lấy cái mới
        blacklistToken(refreshToken, user);

        return AuthResponse.builder()
                .accessToken(jwtService.generateToken(user))
                .refreshToken(jwtService.generateRefreshToken(user))
                .tokenType("Bearer")
                .user(toUserResponse(user))
                .build();
    }

    @Override
    @Transactional
    public void logout(String authHeader, com.example.prjwebservice.model.dto.request.LogoutRequest request) {
        // 1. Blacklist Access Token
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            String accessToken = authHeader.substring(7);
            try {
                String username = jwtService.extractUsername(accessToken);
                userRepository.findByUsername(username).ifPresent(user -> blacklistToken(accessToken, user));
            } catch (Exception ignored) {
                // Token có thể đã hết hạn hoặc lỗi, nhưng vẫn logout được
            }
        }

        // 2. Blacklist Refresh Token
        String refreshToken = request.getRefreshToken();
        try {
            String username = jwtService.extractUsername(refreshToken);
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new ResourceNotFoundException("Người dùng không tồn tại"));
            
            if (jwtService.isTokenValid(refreshToken, user)) {
                blacklistToken(refreshToken, user);
            }
        } catch (Exception e) {
            throw new BadRequestException("Refresh token không hợp lệ");
        }
    }

    private void blacklistToken(String token, User user) {
        if (!tokenBlacklistRepository.existsByTokenString(token)) {
            java.util.Date expiryDate = jwtService.extractExpiration(token);
            java.time.LocalDateTime expiryLDT = expiryDate.toInstant()
                    .atZone(java.time.ZoneId.systemDefault())
                    .toLocalDateTime();

            TokenBlacklist blacklist = TokenBlacklist.builder()
                    .tokenString(token)
                    .revokedAt(LocalDateTime.now())
                    .expiryDate(expiryLDT)
                    .user(user)
                    .build();
            tokenBlacklistRepository.save(blacklist);
        }
    }

    @Override
    @Transactional
    public void changePassword(com.example.prjwebservice.model.dto.request.ChangePasswordRequest request) {
        org.springframework.security.core.Authentication authentication = org.springframework.security.core.context.SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || authentication.getName() == null) {
            throw new ResourceNotFoundException("Chưa xác thực người dùng");
        }
        
        User user = userRepository.findByUsername(authentication.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new BadRequestException("Mật khẩu cũ không chính xác");
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

    }

    private static class ResetTokenInfo {
        String username;
        LocalDateTime expiry;

        ResetTokenInfo(String username, int minutes) {
            this.username = username;
            this.expiry = LocalDateTime.now().plusMinutes(minutes);
        }

        boolean isExpired() {
            return LocalDateTime.now().isAfter(expiry);
        }
    }

    private final java.util.Map<String, ResetTokenInfo> resetTokens = new java.util.concurrent.ConcurrentHashMap<>();

    @Override
    public void forgotPassword(com.example.prjwebservice.model.dto.request.ForgotPasswordRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với email này"));

        String token = java.util.UUID.randomUUID().toString();
        resetTokens.put(token, new ResetTokenInfo(user.getUsername(), 15)); // Hết hạn sau 15 phút
        
        System.out.println("[MOCK EMAIL] Gửi link reset mật khẩu tới " + request.getEmail());
        System.out.println("[MOCK EMAIL] Token reset của bạn là: " + token + " (Hết hạn trong 15 phút)");
    }

    @Override
    @Transactional
    public void resetPassword(com.example.prjwebservice.model.dto.request.ResetPasswordRequest request) {
        ResetTokenInfo tokenInfo = resetTokens.get(request.getToken());
        if (tokenInfo == null || tokenInfo.isExpired()) {
            if (tokenInfo != null) resetTokens.remove(request.getToken());
            throw new com.example.prjwebservice.exception.BadRequestException("Token không hợp lệ hoặc đã hết hạn");
        }

        User user = userRepository.findByUsername(tokenInfo.username)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng"));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);
        resetTokens.remove(request.getToken());
    }

    private UserResponse toUserResponse(User user) {
        return UserResponse.builder()
                .id(user.getId())
                .username(user.getUsername())
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .deleted(user.isDeleted())
                .build();
    }
}
