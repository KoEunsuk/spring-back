package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.response.NotificationResponse;
import com.drive.backend.drive_api.security.SecurityUtil;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import com.drive.backend.drive_api.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notifications")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    // 현재 로그인한 사용자의 모든 알림 목록 조회
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<List<NotificationResponse>>> getMyNotifications() {
        List<NotificationResponse> notifications = notificationService.getNotificationsForUser(getCurrentUser().getUserId());
        return ResponseEntity.ok(ApiResponse.success("알림 목록 조회 성공", notifications));
    }


    // 특정 알림을 '읽음'으로 처리
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<ApiResponse<Void>> markAsRead(@PathVariable Long notificationId) {
        notificationService.markNotificationAsRead(getCurrentUser().getUserId(), notificationId);
        return ResponseEntity.ok(ApiResponse.success("알림을 읽음 처리했습니다."));
    }

    // 현재 사용자 정보를 가져오는 헬퍼 메서드
    private CustomUserDetails getCurrentUser() {
        return SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("인증 정보를 찾을 수 없습니다."));
    }
}
