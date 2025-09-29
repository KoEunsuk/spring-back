package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.NotificationType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)  // 생성일자 자동 기록을 위해 추가
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long notificationId;

    // 알림을 받는 사용자
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "recipient_id", nullable = false)
    private User recipient;

    // 어떤 배차와 관련된 알림인지 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id")
    private Dispatch dispatch;

    @Column(nullable = false)
    private String message;

    @Column(nullable = false)
    private boolean isRead = false; // 기본값은 '안읽음'

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @CreatedDate // 엔티티 생성 시 자동으로 현재 시간 저장
    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    private String relatedUrl; // 알림 클릭 시 이동할 경로

    private Double latitude;
    private Double longitude;

    public Notification(User recipient, Dispatch dispatch, String message, NotificationType type, String relatedUrl) {
        // 위치 정보없는 생성자
        this(recipient, dispatch, message, type, relatedUrl, null, null);
    }

    public Notification(User recipient, Dispatch dispatch, String message, NotificationType type, String relatedUrl, Double latitude, Double longitude) {
        this.recipient = recipient;
        this.dispatch = dispatch;
        this.message = message;
        this.notificationType = type;
        this.relatedUrl = relatedUrl;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    // 읽음 처리 메서드
    public void markAsRead() {
        this.isRead = true;
    }
}
