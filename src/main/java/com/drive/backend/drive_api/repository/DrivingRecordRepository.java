package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.DrivingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DrivingRecordRepository extends JpaRepository<DrivingRecord, Long> {
}
