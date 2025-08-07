package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.dto.DashboardStatusDto;
import com.drive.backend.drive_api.service.DashboardService;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
// @CrossOrigin(origins = "http://localhost:3000") // WebConfig에서 전역 CORS 설정 시 제거 가능
public class DashboardController {

    private final DashboardService dashboardService;

    public DashboardController(DashboardService dashboardService) {
        this.dashboardService = dashboardService;
    }

    @GetMapping("/status")
    public DashboardStatusDto getStats() {
        return dashboardService.getDashboardStats();
    }

    @GetMapping("/recent-alerts")
    public List<AlertDto> getRecentAlerts() {
        return dashboardService.getRecentAlerts(2); // 최근 알림 2개만 반환
    }

    @GetMapping("/graph-data")
    public List<Object> getGraphData() {
        return dashboardService.getGraphData();
    }
}