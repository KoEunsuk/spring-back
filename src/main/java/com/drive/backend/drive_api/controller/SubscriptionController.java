package com.drive.backend.drive_api.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.annotation.SubscribeMapping;
import org.springframework.stereotype.Controller;

import java.security.Principal;

@Slf4j
@Controller
@RequiredArgsConstructor
public class SubscriptionController {

    /**
     * (개인) 알림 채널 구독 처리
     * 클라이언트는 '/user/queue/notifications'로 구독
     * StompHandler와 WebSocketAuthService에서 인증/인가가 이미 처리됨.
     */
    @SubscribeMapping("/user/queue/notifications")
    public void handlePersonalNotificationSubscription(Principal principal) {
        // 이 메서드는 실제로는 비어있거나, 로깅 등의 부가적인 역할만 수행
        // 핵심적인 보안 검사는 StompHandler에서 이미 완료
        if (principal != null) {
            log.info("사용자 '{}'가 개인 알림 채널(/user/queue/notifications)을 구독했습니다.", principal.getName());
        }
    }
}
