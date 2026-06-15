package com.example.prjwebservice.service;

import com.example.prjwebservice.repository.TokenBlacklistRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TokenCleanupService {

    private final TokenBlacklistRepository tokenBlacklistRepository;

    // Chạy vào lúc 1 giờ sáng mỗi ngày
    @Scheduled(cron = "0 0 1 * * ?")
    @Transactional
    public void cleanupExpiredTokens() {
        log.info("Bắt đầu dọn dẹp các token đã hết hạn trong blacklist...");
        tokenBlacklistRepository.deleteByExpiryDateBefore(LocalDateTime.now());
        log.info("Hoàn tất dọn dẹp blacklist.");
    }
}
