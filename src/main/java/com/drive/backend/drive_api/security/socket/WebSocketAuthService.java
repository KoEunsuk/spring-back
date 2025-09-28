package com.drive.backend.drive_api.security.socket;

import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
public class WebSocketAuthService {

    private static final Logger log = LoggerFactory.getLogger(WebSocketAuthService.class);

    public void authorize(Authentication authentication, String destination) {
        if (authentication == null) {
            String errorMessage = "인증 정보가 없는 요청입니다.";
            log.warn("인가 실패: {}, 목적지: {}", errorMessage, destination);
            throw new AccessDeniedException(errorMessage);
        }

        if (!StringUtils.hasText(destination)) {
            return;
        }
        
        // 개인 알림 구독은 인증정보가 있으면 허용
        if (destination.startsWith("/user/queue/")) {
            if (authentication.isAuthenticated()) {
                return;
            }
        }

        // 운행 이벤트 발행은 DRIVER 역할만 허용
        if (destination.startsWith("/app/drive-events")) {
            if (hasAuthority(authentication, "ROLE_DRIVER")) return;
            String errorMessage = "운행 이벤트를 발행할 DRIVER 권한이 없습니다.";
            log.warn("인가 실패: 사용자 '{}', 목적지: {}, 이유: {}", authentication.getName(), destination, errorMessage);
            throw new AccessDeniedException(errorMessage);
        }

        // 그 외 명시되지 않은 모든 경로는 접근 거부
        String errorMessage = "접근 권한이 없는 목적지입니다.";
        log.warn("인가 실패: 사용자 '{}', 목적지: {}, 이유: {}", authentication.getName(), destination, errorMessage);
        throw new AccessDeniedException(errorMessage);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
}
