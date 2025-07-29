package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.dto.DashboardStatusDto;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/dashboard")
@CrossOrigin(origins = "http://localhost:3000")
public class DashboardController {

    @GetMapping("/stats")
    public DashboardStatusDto getStats() {
        return new DashboardStatusDto(1240, 312, 4.7, 8);
    }

    @GetMapping("/recent-alerts")
    public List<AlertDto> getRecentAlerts() {
        return List.of(
                new AlertDto(101L, "졸음 경고", "운전자 박진수님 졸음 감지!", "박진수", LocalDateTime.now().minusMinutes(5), false),
                new AlertDto(102L, "차량 이상", "차량 번호 1234 엔진 이상 감지.", "박윤영", LocalDateTime.now().minusHours(1), false)
        );
    }

    @GetMapping("/graph-data")
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