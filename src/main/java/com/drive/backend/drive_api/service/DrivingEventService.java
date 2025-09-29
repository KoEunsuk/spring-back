package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.websocket.DrivingEventRequest;
import com.drive.backend.drive_api.entity.*;
import com.drive.backend.drive_api.enums.NotificationType;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.DrivingEventRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DrivingEventService {

    private final AdminRepository adminRepository;
    private final DispatchRepository dispatchRepository;
    private final DrivingEventRepository drivingEventRepository;
    private final NotificationService notificationService;

    @Async  // 비동기 실행
    @Transactional
    public void processAndNotify(DrivingEventRequest eventRequest) {
        // 1. dispatchId로 배차 정보와 운행 기록을 찾음
        Dispatch dispatch = dispatchRepository.findById(eventRequest.getDispatchId())
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", eventRequest.getDispatchId()));
        DrivingRecord record = dispatch.getDrivingRecord();

        // 2. 집계 테이블(DrivingRecord)의 카운터 업데이트
        updateRecordCounters(record, eventRequest);

        // 3. 이벤트 발생 이력(DrivingEvent)을 DB에 저장
        DrivingEvent newEvent = new DrivingEvent(record, eventRequest.getEventType(), eventRequest.getEventTimestamp(), eventRequest.getLatitude(), eventRequest.getLongitude());
        record.addDrivingEvent(newEvent);   // DrivingRecord의 이벤트 목록에도 추가하여 양방향 관계를 동기화
        drivingEventRepository.save(newEvent);

        // 4. 알림 메시지와 이동 URL 생성
        String message = String.format("경고: %s 차량(%s)에서 %s 이벤트 발생",
                dispatch.getBus().getVehicleNumber(), dispatch.getDriver().getUsername(), eventRequest.getEventType());
        String url = "/dispatches/" + dispatch.getDispatchId(); // TODO

        // 5. 해당 운수회사의 모든 관리자(Admin)를 찾음
        List<Admin> adminsToNotify = adminRepository.findAllByOperator_OperatorId(dispatch.getBus().getOperator().getOperatorId());

        // 6. 각 관리자에게 알림을 생성하고 실시간으로 발송
        for (Admin admin : adminsToNotify) {
            notificationService.createAndSendNotification(admin, dispatch, message, NotificationType.DRIVING_WARNING, url);
        }
    }

    private void updateRecordCounters(DrivingRecord record, DrivingEventRequest eventRequest) {
        switch (eventRequest.getEventType()) {
            case DROWSINESS:
                record.setDrowsinessCount(record.getDrowsinessCount() + 1);
                break;
            case ACCELERATION:
                record.setAccelerationCount(record.getAccelerationCount() + 1);
                break;
            case BRAKING:
                record.setBrakingCount(record.getBrakingCount() + 1);
                break;
            case ABNORMAL:
                record.setAbnormalCount(record.getAbnormalCount() + 1);
                break;
        }

        record.setDrivingScore(Math.max(0, record.getDrivingScore() - 10));
    }
}
