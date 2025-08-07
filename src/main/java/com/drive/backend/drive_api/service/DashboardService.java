package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.dto.DashboardStatusDto;
import com.drive.backend.drive_api.repository.AlertRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class DashboardService {

    private final AlertRepository alertRepository;
    private final DriverRepository driverRepository;

    public DashboardService(AlertRepository alertRepository, DriverRepository driverRepository) {
        this.alertRepository = alertRepository;
        this.driverRepository = driverRepository;
    }

    public DashboardStatusDto getDashboardStats() {
        int totalRides = 1240; // 임시 데이터, 실제 DB에서 조회
        int totalDrivers = driverRepository.findAll().size(); // DriverRepository 사용
        double avgSatisfaction = 4.7; // 임시 데이터
        int todayAlerts = (int) alertRepository.findAll().stream() // 오늘 알림 수 계산
                .filter(a -> a.getAlertTime().toLocalDate().isEqual(LocalDateTime.now().toLocalDate()))
                .count();
        return new DashboardStatusDto(totalRides, totalDrivers, avgSatisfaction, todayAlerts);
    }

    public List<AlertDto> getRecentAlerts(int limit) {
        return alertRepository.findRecentAlerts(limit);
    }

    public List<Object> getGraphData() {
        return List.of(
                List.of("1월", 150),
                List.of("2월", 180),
                List.of("3월", 200),
                List.of("4월", 190),
                List.of("5월", 220)
        );
    }
}