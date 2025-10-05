package com.drive.backend.drive_api.controller.websocket;

import com.drive.backend.drive_api.dto.websocket.ObdDataRequest;
import com.drive.backend.drive_api.service.ObdService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

@Controller
@RequiredArgsConstructor
public class ObdController {

    private final ObdService obdService;

    // 운전자 앱이 "/app/obd/update" 경로로 odb 정보를 보내면 동작
    @MessageMapping("/obd/update")
    public void handleObdUpdate(@Valid ObdDataRequest request) {
        obdService.processAndBroadcastObdData(request);
    }
}
