package com.drive.backend.drive_api.controller.websocket;

import com.drive.backend.drive_api.dto.websocket.LocationUpdateRequest;
import com.drive.backend.drive_api.service.LocationService;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class LocationController {

    private final LocationService locationService;

    // 운전자 앱이  "/app/location/update" 경로로 위치 정보를 보내면 이 메서드가 동작
    @MessageMapping("/location/update")
    public void handleLocationUpdate(LocationUpdateRequest request) {
        locationService.processAndBroadcastLocation(request);
    }
}
