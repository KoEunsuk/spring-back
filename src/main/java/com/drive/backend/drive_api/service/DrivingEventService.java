package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.websocket.DrivingEventRequest;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.DrivingEvent;
import com.drive.backend.drive_api.entity.DrivingRecord;
import com.drive.backend.drive_api.enums.NotificationType;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.DrivingEventRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DrivingEventService {

    private final AdminRepository adminRepository;
    private final DispatchRepository dispatchRepository;
    private final DrivingEventRepository drivingEventRepository;
    private final NotificationService notificationService;

    @Async  // 비동기 실행 - 병목 방지 + 즉각적인 응답 필요없음
    @Transactional
    public void processAndNotify(DrivingEventRequest eventRequest) {
        try {
            // dispatchId로 배차 정보와 운행 기록을 찾음
            Dispatch dispatch = dispatchRepository.findById(eventRequest.getDispatchId())
                    .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", eventRequest.getDispatchId()));
            DrivingRecord record = dispatch.getDrivingRecord();

            // 이벤트 발생 이력(DrivingEvent)을 DB에 저장 - 이벤트를 별도 트랜잭션으로 저장 (항상 커밋됨)
            DrivingEvent newEvent = saveEventWithNewTransaction(record, eventRequest);

            // 집계 테이블(DrivingRecord)의 카운터 업데이트 (낙관적 락 재시도)
            updateRecordWithRetry(record, eventRequest);


            // 관리자에게 실시간 알림 전송
            sendEventNotification(dispatch, eventRequest);

        } catch (DataIntegrityViolationException e) {
            // 중복 이벤트 무시 (Unique 제약조건 위반)
            log.warn("[중복 이벤트 무시] 동일 이벤트 요청: {}", eventRequest);
        } catch (OptimisticLockingFailureException e) {
            // 낙관적 락 충돌 (DrivingRecord 갱신 실패)
            log.error("[DrivingRecord 충돌] {}", e.getMessage());
        } catch (Exception e) {
            // 기타 예외
            log.error("[DrivingEvent 처리 실패] {}", e.getMessage(), e);
        }
    }

    // 이벤트만 별도 트랜잭션으로 저장 → 알림 실패해도 DB에는 남음
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public DrivingEvent saveEventWithNewTransaction(DrivingRecord record, DrivingEventRequest eventRequest) {
        try {
            DrivingEvent newEvent = new DrivingEvent(
                    record,
                    eventRequest.getEventType(),
                    eventRequest.getEventTimestamp(),
                    eventRequest.getLatitude(),
                    eventRequest.getLongitude()
            );

            record.addDrivingEvent(newEvent); // 양방향 관계 동기화
            DrivingEvent saved = drivingEventRepository.save(newEvent);

            log.debug("[DrivingEvent 저장 성공] recordId={}, eventType={}, timestamp={}",
                    record.getId(), eventRequest.getEventType(), eventRequest.getEventTimestamp());

            return saved;
        } catch (DataIntegrityViolationException e) {
            log.warn("[DrivingEvent 저장 중복 무시] {}", e.getMessage());
            throw e; // 상위에서 잡음
        } catch (Exception e) {
            log.error("[DrivingEvent 저장 실패] {}", e.getMessage(), e);
            throw e;
        }
    }

    // DrivingRecord 카운터 갱신 (낙관적 락 충돌 시 최대 2회 재시도)
    private void updateRecordWithRetry(DrivingRecord record, DrivingEventRequest eventRequest) { // TODO: 가중치 설정
        int retryCount = 0;
        boolean success = false;

        while (retryCount < 2 && !success) {
            try {
                switch (eventRequest.getEventType()) {
                    case DROWSINESS -> record.setDrowsinessCount(record.getDrowsinessCount() + 1);
                    case ACCELERATION -> record.setAccelerationCount(record.getAccelerationCount() + 1);
                    case BRAKING -> record.setBrakingCount(record.getBrakingCount() + 1);
                    case ABNORMAL -> record.setAbnormalCount(record.getAbnormalCount() + 1);
                }
                record.setDrivingScore(Math.max(0, record.getDrivingScore() - 10));
                success = true;
            } catch (OptimisticLockingFailureException e) {
                retryCount++;
                log.warn("DrivingRecord 낙관적 락 충돌, 재시도 중 ({}/2)", retryCount);
                try {
                    Thread.sleep(100L * retryCount);
                } catch (InterruptedException ignored) {
                }
            }
        }

        if (!success) {
            log.error("DrivingRecord 갱신 실패 - 이벤트는 저장되었으나 데이터 불일치 가능성 있음");
        }
    }

    // 관리자에게 실시간 알림 전송
    private void sendEventNotification(Dispatch dispatch, DrivingEventRequest eventRequest) {
        try {
            String message = String.format(
                    "경고: %s 차량(%s)에서 %s 이벤트 발생",
                    dispatch.getBus().getVehicleNumber(),
                    dispatch.getDriver().getUsername(),
                    eventRequest.getEventType()
            );
            String url = "/dispatches/" + dispatch.getDispatchId(); // todo

            List<Admin> admins = adminRepository.findAllByOperator_OperatorId(
                    dispatch.getBus().getOperator().getOperatorId()
            );

            for (Admin admin : admins) {
                notificationService.createAndSendNotification(
                        admin,
                        dispatch,
                        message,
                        NotificationType.DRIVING_WARNING,
                        url,
                        eventRequest.getLatitude(),
                        eventRequest.getLongitude()
                );
            }
            log.debug("[알림 전송 완료] dispatchId={}, admins={}", dispatch.getDispatchId(), admins.size());
        } catch (Exception e) {
            log.error("[알림 전송 실패] dispatchId={}, error={}", dispatch.getDispatchId(), e.getMessage());
        }
    }
}
