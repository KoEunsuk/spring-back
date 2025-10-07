package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.RefreshToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    // 사용자 ID로 리프레시 토큰을 찾는 메서드
    Optional<RefreshToken> findByUser_UserId(Long userId);
}
