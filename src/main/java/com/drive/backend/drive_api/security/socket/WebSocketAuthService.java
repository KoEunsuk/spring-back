package com.drive.backend.drive_api.security.socket;

import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
public class WebSocketAuthService {
    public void authorize(Authentication authentication, String destination) {
        if (authentication == null) {
            throw new AccessDeniedException("인증 정보가 없는 요청입니다.");
        }
        if (!StringUtils.hasText(destination)) {
            return;
        }

        if (destination.startsWith("/topic/operator/")) {
            if (hasAuthority(authentication, "ROLE_ADMIN")) return;
            throw new AccessDeniedException("해당 토픽을 구독할 ADMIN 권한이 없습니다.");
        }

        if (destination.startsWith("/app/")) {
            if (hasAuthority(authentication, "ROLE_DRIVER")) return;
            throw new AccessDeniedException("해당 경로에 메시지를 보낼 DRIVER 권한이 없습니다.");
        }

        throw new AccessDeniedException("접근 권한이 없는 목적지입니다.");
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> grantedAuthority.getAuthority().equals(authority));
    }
}
