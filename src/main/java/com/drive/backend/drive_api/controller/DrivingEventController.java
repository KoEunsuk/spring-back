package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.websocket.DrivingEventRequest;
import com.drive.backend.drive_api.service.DrivingEventService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class DrivingEventController {

    private final DrivingEventService drivingEventService;

    // 클라이언트가 "/app/drive-events"로 메시지를 보내면 이 메서드가 처리
    @MessageMapping("/drive-events")
    public void handleDrivingEvent(DrivingEventRequest eventRequest) {
        // 받은 이벤트를 즉시 서비스로 넘겨 비동기 처리
        drivingEventService.processAndNotify(eventRequest);
    }
}
