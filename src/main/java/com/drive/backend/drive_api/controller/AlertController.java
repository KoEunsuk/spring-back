package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.service.AlertService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alerts")
public class AlertController {

    private final AlertService alertService;

    public AlertController(AlertService alertService) {
        this.alertService = alertService;
    }

    @GetMapping // 모든 알림 조회
    public List<AlertDto> getAllAlerts() {
        return alertService.getAllAlerts();
    }

    @GetMapping("/{id}") // ID로 특정 알림 조회
    public AlertDto getAlertById(@PathVariable Long id) {
        return alertService.getAlertById(id);
    }

    @PostMapping // 새 알림 등록
    public ResponseEntity<AlertDto> addAlert(@RequestBody AlertDto alertDto) {
        AlertDto newAlert = alertService.addAlert(alertDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newAlert); 
    }

    @PutMapping("/{id}") // 알림 정보 업데이트
    public AlertDto updateAlert(@PathVariable Long id, @RequestBody AlertDto alertDto) {
        return alertService.updateAlert(id, alertDto);
    }

    @DeleteMapping("/{id}") // 알림 삭제
    public ResponseEntity<String> deleteAlert(@PathVariable Long id) {
        alertService.deleteAlert(id);
        return ResponseEntity.ok("알림 삭제 완료");
    }

    @GetMapping("/driver/{driverId}") // 특정 운전자의 알림 조회
    public List<AlertDto> getAlertsByDriverId(@PathVariable Long driverId) {
        return alertService.getAlertsByDriverId(driverId);
    }
}