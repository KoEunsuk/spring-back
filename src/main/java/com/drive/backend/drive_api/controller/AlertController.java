package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.AlertDto;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
@CrossOrigin(origins = "http://localhost:3000")
public class AlertController {

    private static List<AlertDto> alerts = new ArrayList<>(List.of(
            new AlertDto(1L, "졸음 경고", "운전자 박진수 졸음 감지!", "박진수", LocalDateTime.now().minusMinutes(10), false),
            new AlertDto(2L, "차량 이상", "차량 1234 엔진 이상 감지", "박윤영", LocalDateTime.now().minusHours(2), true)
    ));

    @GetMapping
    public List<AlertDto> getAllAlerts() {
        return alerts;
    }

    @PutMapping("/{id}/read")
    public AlertDto markAsRead(@PathVariable Long id) {
        for (AlertDto alert : alerts) {
            if (alert.getId().equals(id)) {
                alert.setRead(true);
                return alert;
            }
        }
        throw new RuntimeException("알림을 찾을 수 없습니다.");
    }

    @GetMapping("/stats")
    public Map<String, Long> getAlertStats() {
        return alerts.stream()
                .collect(Collectors.groupingBy(AlertDto::getType, Collectors.counting()));
    }
}