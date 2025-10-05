package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.websocket.ObdDataRequest;
import com.drive.backend.drive_api.dto.websocket.ObdDataResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ObdService {

    private final SimpMessagingTemplate messagingTemplate;

    @Async // WebSocket 요청 처리를 비동기로 수행하여 병목 방지
    //TODO : DB 관련 작업 추가시 Transactional + 엔티티 클래스 생성
    public void processAndBroadcastObdData(ObdDataRequest request) {
        try {
            // 1. 응답 DTO 생성
            ObdDataResponse response = ObdDataResponse.from(request);

            // 2. 전송할 목적지(토픽) 주소 생성
            String destination = "/topic/dispatch/" + request.getDispatchId() + "/obd";

            // 3. 해당 토픽을 구독 중인 클라이언트에게 메시지 전송
            messagingTemplate.convertAndSend(destination, response);

        } catch (Exception e) {
            log.error("[OBD] OBD 데이터 처리 및 방송 실패: {}", e.getMessage(), e);
        }
    }
}
