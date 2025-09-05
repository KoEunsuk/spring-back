package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.AlertDto;
import com.drive.backend.drive_api.entity.Alert;
import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AlertRepository;
import com.drive.backend.drive_api.repository.BusRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class AlertService {

    private final AlertRepository alertRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;

    public AlertService(AlertRepository alertRepository, DriverRepository driverRepository, BusRepository busRepository) {
        this.alertRepository = alertRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
    }

    // AlertDto를 Alert 엔티티로 변환
    private Alert toEntity(AlertDto dto) {
        Alert alert = new Alert();
        if (dto.getAlertId() != null) {
            alert.setAlertId(dto.getAlertId()); // ID가 있으면 업데이트 시 사용
        }
        // Driver 연결
        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", dto.getDriverId()));
            alert.setDriver(driver);
        }
        // Bus 연결
        if (dto.getBusId() != null) {
            Bus bus = busRepository.findById(dto.getBusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", dto.getBusId()));
            alert.setBus(bus);
        }
        alert.setAlertType(dto.getAlertType());
        alert.setSeverity(dto.getSeverity());
        alert.setMessage(dto.getMessage());
        alert.setTimestamp(dto.getTimestamp());
        return alert;
    }

    // Alert 엔티티를 AlertDto로 변환
    private AlertDto toDto(Alert entity) {
        return new AlertDto(
                entity.getAlertId(),
                entity.getDriver() != null ? entity.getDriver().getDriverId() : null, // Driver ID
                entity.getBus() != null ? entity.getBus().getBusId() : null,         // Bus ID
                entity.getAlertType(),
                entity.getSeverity(),
                entity.getMessage(),
                entity.getTimestamp(),
                entity.getDriver() != null ? entity.getDriver().getDriverName() : null,         // Driver 이름
                entity.getBus() != null ? entity.getBus().getVehicleNumber() : null             // Bus 차량 번호
        );
    }

    // 모든 알림 조회
    @Transactional(readOnly = true)
    public List<AlertDto> getAllAlerts() {
        return alertRepository.findAll().stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }

    // ID로 알림 조회
    @Transactional(readOnly = true)
    public AlertDto getAlertById(Long id) {
        Alert alert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        return toDto(alert);
    }

    // 새 알림 등록 (감지 시스템에서 호출)
    @Transactional
    public AlertDto addAlert(AlertDto alertDto) {
        // 알림 생성 시 타임스탬프가 없으면 현재 시간으로 설정한다. (선택적)
        if (alertDto.getTimestamp() == null) {
            alertDto.setTimestamp(LocalDateTime.now());
        }
        Alert alert = toEntity(alertDto); // DTO를 엔티티로 변환
        Alert savedAlert = alertRepository.save(alert); // DB에 저장
        return toDto(savedAlert); // 저장된 엔티티 반환
    }

    // 알림 정보 업데이트
    @Transactional
    public AlertDto updateAlert(Long id, AlertDto alertDto) {
        Alert existingAlert = alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        existingAlert.setAlertType(alertDto.getAlertType());
        existingAlert.setSeverity(alertDto.getSeverity());
        existingAlert.setMessage(alertDto.getMessage());
        existingAlert.setTimestamp(alertDto.getTimestamp());

        Alert updatedAlert = alertRepository.save(existingAlert);
        return toDto(updatedAlert);
    }

    // 알림 삭제
    @Transactional
    public void deleteAlert(Long id) {
        alertRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", "id", id));
        alertRepository.deleteById(id);
    }


    // 특정 운전자의 알림 조회
    @Transactional(readOnly = true)
    public List<AlertDto> getAlertsByDriverId(Long driverId) {
        return alertRepository.findByDriverDriverId(driverId).stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}