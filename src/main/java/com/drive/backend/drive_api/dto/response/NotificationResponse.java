package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Notification;
import com.drive.backend.drive_api.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;

public class NotificationResponse {
    @Getter private final Long notificationId;
    @Getter private final String message;

    private final boolean isRead;

    @Getter private final NotificationType notificationType;
    @Getter private final String relatedUrl;
    @Getter private final LocalDateTime createdAt;

    // 유연한 UI 구성을 위한 상세 정보
    @Getter private final Long dispatchId;
    @Getter private final String vehicleNumber;
    @Getter private final String driverName;

    // 알림 발생 위치
    @Getter private final Double latitude;
    @Getter private final Double longitude;

    @JsonProperty("isRead")
    public boolean isRead() {
        return this.isRead;
    }

    // private 생성자
    private NotificationResponse(Notification notification) {
        this.notificationId = notification.getNotificationId();
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.notificationType = notification.getNotificationType();
        this.relatedUrl = notification.getRelatedUrl();
        this.createdAt = notification.getCreatedAt();
        this.latitude = notification.getLatitude();
        this.longitude = notification.getLongitude();

        // Dispatch 정보가 있을 경우에만 관련 데이터를 채움
        if (notification.getDispatch() != null) {
            this.dispatchId = notification.getDispatch().getDispatchId();
            this.vehicleNumber = notification.getDispatch().getBus().getVehicleNumber();
            this.driverName = notification.getDispatch().getDriver().getUsername();
        } else {
            this.dispatchId = null;
            this.vehicleNumber = null;
            this.driverName = null;
        }
    }

    // 엔티티를 DTO로 변환하는 정적 팩토리 메서드
    public static NotificationResponse from(Notification notification) {
        return new NotificationResponse(notification);
    }
}
