package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.DrivingEventType;
import lombok.Getter;

import java.time.LocalDateTime;

// 모바일 클라이언트가 서버로 보내는 운행 이벤트 메세지
@Getter
public class DrivingEventRequest {
    private Long dispatchId;
    private DrivingEventType eventType;
    private LocalDateTime eventTimestamp;
}
