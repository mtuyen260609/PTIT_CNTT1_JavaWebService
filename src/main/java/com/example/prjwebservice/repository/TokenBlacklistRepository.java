package com.example.prjwebservice.repository;

import com.example.prjwebservice.model.entity.TokenBlacklist;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TokenBlacklistRepository extends JpaRepository<TokenBlacklist, Long> {
    boolean existsByTokenString(String tokenString);
}
