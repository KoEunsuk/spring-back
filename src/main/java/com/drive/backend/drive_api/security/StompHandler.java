package com.drive.backend.drive_api.security;

import com.drive.backend.drive_api.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

        // 1. STOMP CONNECT 단계인지 확인
        if (StompCommand.CONNECT.equals(accessor.getCommand())) {

            // 2. 메시지 헤더에서 "Authorization" 값을 가져와 Bearer 토큰을 추출
            String jwt = resolveToken(accessor.getFirstNativeHeader("Authorization"));

            // 3. 토큰 유효성 검사
            if (StringUtils.hasText(jwt) && tokenProvider.validateJwtToken(jwt)) {

                // 4. 토큰이 유효하면, Authentication 객체를 가져와 SecurityContext에 저장하지 않고,
                //    accessor의 user 헤더에 직접 설정하여 해당 WebSocket 세션에 대한 인증 정보로 사용
                Authentication authentication = tokenProvider.getAuthentication(jwt);
                accessor.setUser(authentication);
            }
        }
        return message;
    }

    private String resolveToken(String bearerToken) {
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}