package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Alert;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AlertRepository extends JpaRepository<Alert, Long> {

    List<Alert> findByDriverDriverId(Long driverId); // Driver ID로 알림 찾기
    List<Alert> findByBusBusId(Long busId); // Bus ID로 알림 찾기
    List<Alert> findByAlertType(String alertType); // 알림 타입으로 알림 찾는법
    List<Alert> findByTimestampBetween(LocalDateTime start, LocalDateTime end); // 특정 기간 알림 찾기
}