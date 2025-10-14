package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.Notification;
import com.drive.backend.drive_api.enums.NotificationType;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

public class NotificationResponse {
    @Getter private final Long notificationId;
    @Getter private final String message;

    private final boolean isRead;

    @Getter private final NotificationType notificationType;
    @Getter private final String relatedUrl;
    @Getter private final LocalDateTime createdAt;

    // 타입별 확장 데이터
    @Getter private final Map<String, Object> payload;

    @JsonProperty("isRead")
    public boolean isRead() {
        return this.isRead;
    }

    // private 생성자
    private NotificationResponse(Notification notification, Map<String, Object> payload) {
        this.notificationId = notification.getNotificationId();
        this.message = notification.getMessage();
        this.isRead = notification.isRead();
        this.notificationType = notification.getNotificationType();
        this.relatedUrl = notification.getRelatedUrl();
        this.createdAt = notification.getCreatedAt();
        this.payload = payload;
    }

    public static NotificationResponse from(Notification notification) {
        Map<String, Object> payload = new HashMap<>();

        switch (notification.getNotificationType()) {
            case DRIVING_WARNING -> {
                Dispatch dispatch = notification.getDispatch();
                if (dispatch != null) {
                    payload.put("dispatchId", dispatch.getDispatchId());
                    payload.put("vehicleNumber", dispatch.getBus().getVehicleNumber());
                    payload.put("driverName", dispatch.getDriver().getUsername());
                }
                payload.put("latitude", notification.getLatitude());
                payload.put("longitude", notification.getLongitude());
            }
            case NEW_DISPATCH_ASSIGNED, DISPATCH_STARTED, DISPATCH_CANCELED, DISPATCH_ENDED -> {
                Dispatch dispatch = notification.getDispatch();
                if (dispatch != null) {
                    payload.put("dispatchId", dispatch.getDispatchId());
                    payload.put("vehicleNumber", dispatch.getBus().getVehicleNumber());
                    payload.put("driverName", dispatch.getDriver().getUsername());
                    payload.put("scheduledDepartureTime", dispatch.getScheduledDepartureTime());
                }
            }
            default -> {
                // 다른 타입은 payload 없음
            }
        }

        return new NotificationResponse(notification, payload.isEmpty() ? null : payload);
    }
}
