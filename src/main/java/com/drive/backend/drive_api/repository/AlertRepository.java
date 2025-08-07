package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.dto.AlertDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class AlertRepository {

    private static List<AlertDto> alerts = Collections.synchronizedList(new ArrayList<>(List.of(
            new AlertDto(1L, "졸음 경고", "운전자 박진수 졸음 감지!", "박진수", LocalDateTime.now().minusMinutes(30), false),
            new AlertDto(2L, "차량 이상", "차량 번호 1234 엔진 이상 감지", "박윤영", LocalDateTime.now().minusHours(1), true),
            new AlertDto(3L, "졸음 경고", "운전자 고은석 졸음 감지!", "고은석", LocalDateTime.now().minusHours(3), false),
            new AlertDto(4L, "차량 이상", "차량 번호 5678 타이어 공기압 이상", "정의태", LocalDateTime.now().minusDays(1), false)
    )));
    private static AtomicLong nextId = new AtomicLong(4);

    public List<AlertDto> findAll() {
        return new ArrayList<>(alerts);
    }

    public List<AlertDto> findRecentAlerts(int limit) {
        return alerts.stream()
                .sorted((a1, a2) -> a2.getAlertTime().compareTo(a1.getAlertTime()))
                .limit(limit)
                .collect(Collectors.toList());
    }

    public Optional<AlertDto> findById(Long id) {
        return alerts.stream()
                .filter(a -> a.getId().equals(id))
                .findFirst();
    }

    public AlertDto save(AlertDto alert) {
        if (alert.getId() == null) {
            alert.setId(nextId.incrementAndGet());
            alerts.add(alert);
        } else {
            findById(alert.getId()).ifPresent(existingAlert -> {
                existingAlert.setType(alert.getType());
                existingAlert.setMessage(alert.getMessage());
                existingAlert.setDriverName(alert.getDriverName());
                existingAlert.setAlertTime(alert.getAlertTime());
                existingAlert.setRead(alert.isRead());
            });
        }
        return alert;
    }

    public Map<String, Long> countAlertsByType() {
        return alerts.stream()
                .collect(Collectors.groupingBy(AlertDto::getType, Collectors.counting()));
    }
}
