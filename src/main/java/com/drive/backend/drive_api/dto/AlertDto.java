package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AlertDto {
    private Long id;          // 알림 고유 ID
    private String type;      // 알림 타입 (예: "졸음 경고", "차량 이상")
    private String message;   // 알림 내용
    private String driverName; // 관련 운전자 이름
    private LocalDateTime alertTime; // 알림 발생 시간
    private boolean isRead;   // 알림 확인 여부
}
