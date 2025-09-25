package com.drive.backend.drive_api.dto.websocket;

import com.drive.backend.drive_api.enums.DrivingEventType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

// 서버가 관리자 웹 클라이언트로 보내는 경고 알림 메시지
@Getter
@AllArgsConstructor
public class DrivingWarningNotification {
    private Long dispatchId;
    private String vehicleNumber;
    private String driverName;
    private DrivingEventType eventType;
    private LocalDateTime eventTimestamp;
    private String message;
}
