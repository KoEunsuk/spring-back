package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.dto.DashboardStatusDto;
import com.drive.backend.drive_api.dto.DispatchDto;
import com.drive.backend.drive_api.entity.Alert;
import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AlertRepository;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class DashboardService {

    private final AlertRepository alertRepository;
    private final DriverRepository driverRepository;
    private final DispatchRepository dispatchRepository;

    public DashboardService(AlertRepository alertRepository, DriverRepository driverRepository, DispatchRepository dispatchRepository) {
        this.alertRepository = alertRepository;
        this.driverRepository = driverRepository;
        this.dispatchRepository = dispatchRepository;
    }

    private AlertDto toAlertDto(Alert entity) {
        return new AlertDto(
                entity.getAlertId(),
                entity.getDriver() != null ? entity.getDriver().getDriverId() : null,
                entity.getBus() != null ? entity.getBus().getBusId() : null,
                entity.getAlertType(),
                entity.getSeverity(),
                entity.getMessage(),
                entity.getTimestamp(),
                entity.getDriver() != null ? entity.getDriver().getDriverName() : null,
                entity.getBus() != null ? entity.getBus().getVehicleNumber() : null
        );
    }

    //대시 보드 상태조회
    @Transactional(readOnly = true)
    public DashboardStatusDto getDashboardStats() {
        long totalRides = dispatchRepository.count();
        long totalDrivers = driverRepository.count();
        double avgSatisfaction = 5.0;
        LocalDateTime startOfDay = LocalDateTime.now().withHour(0).withMinute(0).withSecond(0).withNano(0);
        LocalDateTime endOfDay = LocalDateTime.now().withHour(23).withMinute(59).withSecond(59).withNano(999999999);
        long todayAlerts = alertRepository.findByTimestampBetween(startOfDay, endOfDay).size();

        return new DashboardStatusDto(totalRides, totalDrivers, avgSatisfaction, todayAlerts);
    }

    // 최근 알림 목록 조회
    @Transactional(readOnly = true)
    public List<AlertDto> getRecentAlerts(int limit) {
        return alertRepository.findAll().stream()
                .sorted(Comparator.comparing(Alert::getTimestamp).reversed()) // timestamp 기준으로 내림차순 정렬
                .limit(limit) //
                .map(this::toAlertDto)
                .collect(Collectors.toList()); // List<AlertDto>로 수집해서 반환함.
    }
}