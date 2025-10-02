package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Admin 중에서 특정 operator에 속한 사용자 찾기
    List<Admin> findAllByOperator_OperatorId(Long operatorId);
}