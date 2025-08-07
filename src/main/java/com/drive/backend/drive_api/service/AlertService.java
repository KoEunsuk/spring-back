package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AlertRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class AlertService {

    private final AlertRepository alertRepository;

    public AlertService(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<AlertDto> getAllAlerts() {
        return alertRepository.findAll();
    }

    public AlertDto markAsRead(Long id) {
        AlertDto alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        alert.setRead(true);
        return alertRepository.save(alert);
    }

    public Map<String, Long> getAlertStats() {
        return alertRepository.countAlertsByType();
    }
}