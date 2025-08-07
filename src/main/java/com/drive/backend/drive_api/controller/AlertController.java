package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.service.AlertService;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/alerts")
// @CrossOrigin(origins = "http://localhost:3000") // WebConfig에서 전역 CORS 설정 시 제거 가능
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping
    public List<AlertDto> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @PutMapping("/{id}/read")
    public AlertDto markAsRead(@PathVariable Long id) {
        return alertService.markAsRead(id);
    }

    @GetMapping("/status")
    public Map<String, Long> getAlertStats() {
        return alertService.getAlertStats();
    }
}