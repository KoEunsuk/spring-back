package com.drive.backend.drive_api.controller.websocket;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.response.NotificationResponse;
import com.drive.backend.drive_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.drive.backend.drive_api.security.SecurityUtil.getAuthenticatedUser;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 현재 로그인한 사용자의 모든 알림 목록 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(getAuthenticatedUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success("알림 목록 조회 성공", notifications));
    }


    // 특정 알림을 '읽음'으로 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(getAuthenticatedUser().getUserId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success("알림을 읽음 처리했습니다."));
    }
}
