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

        if (destination.startsWith("/topic/operator/")) {
            if (hasAuthority(authentication, "ROLE_ADMIN")) return;
            String errorMessage = "해당 토픽을 구독할 ADMIN 권한이 없습니다.";
            log.warn("인가 실패: 사용자 '{}', 목적지: {}, 이유: {}", authentication.getName(), destination, errorMessage);
            throw new AccessDeniedException(errorMessage);
        }

        if (destination.startsWith("/app/")) {
            if (hasAuthority(authentication, "ROLE_DRIVER")) return;
            String errorMessage = "해당 경로에 메시지를 보낼 DRIVER 권한이 없습니다.";
            log.warn("인가 실패: 사용자 '{}', 목적지: {}, 이유: {}", authentication.getName(), destination, errorMessage);
            throw new AccessDeniedException(errorMessage);
        }

        String errorMessage = "접근 권한이 없는 목적지입니다.";
        log.warn("인가 실패: 사용자 '{}', 목적지: {}, 이유: {}", authentication.getName(), destination, errorMessage);
        throw new AccessDeniedException(errorMessage);
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
}
