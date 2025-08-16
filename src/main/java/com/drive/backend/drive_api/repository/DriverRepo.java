package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Driver;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DriverRepo extends JpaRepository<Driver, Long> {
    // 기본 CRUD 메서드(findAll, findById, save, deleteById) 자동 제공
}
