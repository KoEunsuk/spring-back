package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AlertDto {
    private Long alertId; // 알림 ID
    private Long driverId; // 관련 운전자의 ID
    private Long busId;    // 관련 버스의 ID
    private String alertType; // 알림 종류
    private String severity; // 알림 심각도
    private String message; // 알림 내용
    private LocalDateTime timestamp; // 알림 발생 시간

    private String driverName; // 운전자 이름
    private String busVehicleNumber; // 버스 차량 번호
}