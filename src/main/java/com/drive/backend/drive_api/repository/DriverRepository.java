package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    // 기본 CRUD는 JpaRepository가 전부 제공함

    Optional<Driver> findByDriverName(String driverName);
}