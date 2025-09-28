package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.response.NotificationResponse;
import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.Notification;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.enums.NotificationType;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.NotificationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final SimpMessagingTemplate messagingTemplate;

    @Transactional
    public void createAndSendNotification(User recipient, Dispatch dispatch, String message, NotificationType type, String url) {
        // 1. 알림을 DB에 저장
        Notification notification = new Notification(recipient, dispatch, message, type, url);

        recipient.addNotification(notification);
        dispatch.addNotification(notification);

        notificationRepository.save(notification);

        // 2. DTO로 변환
        NotificationResponse notificationDto = NotificationResponse.from(notification);

        // 3. 해당 사용자 개인에게만 웹소켓 메세지 전송
        //    /user/queue/notifications 채널을 구독 중인 특정 사용자에게만 메시지가 전달됨
        messagingTemplate.convertAndSendToUser(
                recipient.getEmail(),         // Spring Security User Principal의 name (우리 시스템에서는 email)
                "/queue/notifications",       // 개인 알림을 위한 구독 경로
                notificationDto
        );
    }

    // 특정 사용자의 모든 알림 목록을 조회하는 메서드
    @Transactional(readOnly = true)
    public List<NotificationResponse> getNotificationsForUser(Long userId) {
        List<Notification> notifications = notificationRepository.findByRecipientUserIdOrderByCreatedAtDesc(userId);
        return notifications.stream()
                .map(NotificationResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 알림을 '읽음'으로 처리하는 메서드
    @Transactional
    public void markNotificationAsRead(Long userId, Long notificationId) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification", "id", notificationId));

        // 알림의 수신자와 현재 사용자가 일치하는지 확인
        if (!notification.getRecipient().getUserId().equals(userId)) {
            throw new AccessDeniedException("자신의 알림만 읽음 처리할 수 있습니다.");
        }

        notification.markAsRead();
    }
}
