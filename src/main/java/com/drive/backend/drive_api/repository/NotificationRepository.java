package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Notification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificationRepository extends JpaRepository<Notification, Long> {
    // 특정 사용자의 모든 알림을 최신순으로 조회
    List<Notification> findByRecipientUserIdOrderByCreatedAtDesc(Long recipientId);

    // 특정 사용자의 안읽은 알림을 최신순으로 조회
    List<Notification> findByRecipientUserIdAndIsReadFalseOrderByCreatedAtDesc(Long recipientId);

}
