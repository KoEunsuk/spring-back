package com.drive.backend.drive_api.repository;
import com.drive.backend.drive_api.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // 특정 User에 연결된 Admin을 찾기 위한 커스텀 메서드
    Optional<Admin> findByUserId(Long userId);
}