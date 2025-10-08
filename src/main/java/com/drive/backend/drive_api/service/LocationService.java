package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.websocket.LocationUpdateRequest;
import com.drive.backend.drive_api.dto.websocket.LocationUpdateResponse;
import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.LocationHistory;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.LocationHistoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class LocationService {

    private final DispatchRepository dispatchRepository;
    private final LocationHistoryRepository locationHistoryRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Async
    @Transactional
    public void processAndBroadcastLocation(LocationUpdateRequest request) {
        try {
            // 1. 배차 정보 조회
            Dispatch dispatch = dispatchRepository.findById(request.getDispatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", request.getDispatchId()));

            // 2. 위치 이력 생성 및 DB 저장
            LocationHistory newLocation = new LocationHistory(dispatch, request.getLatitude(), request.getLongitude());
            dispatch.addLocationHistory(newLocation);
            locationHistoryRepository.save(newLocation);

            // 3. 관리자에게 브로드캐스팅할 알림 DTO 생성
            LocationUpdateResponse notification = LocationUpdateResponse.from(dispatch, newLocation);

            // 4. 특정 배차를 구독 중인 모든 관리자에게 실시간 위치 정보 전송
            String destination = "/topic/dispatch/" + dispatch.getDispatchId() + "/location";
            messagingTemplate.convertAndSend(destination, notification);
        } catch (Exception e) {
            log.error("[Location] 위치 업데이트 실패: {}", e.getMessage(), e);
        }
    }

}
