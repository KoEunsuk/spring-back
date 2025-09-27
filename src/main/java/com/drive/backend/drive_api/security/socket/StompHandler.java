package com.drive.backend.drive_api.security.socket;

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

import java.util.Optional;

@Component
@RequiredArgsConstructor
public class StompHandler implements ChannelInterceptor {

    private final JwtTokenProvider tokenProvider;
    private final WebSocketAuthService webSocketAuthService;

    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);
        StompCommand command = accessor.getCommand();

        // 1. JWT를 이용한 인증 처리 (모든 메시지에 대해)
        Authentication authentication = authenticate(accessor);

        // 2. 인가 처리 (SUBSCRIBE, SEND 명령어에 대해서만 수행)
        if (command != null) {
            if (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
                // 인가 처리를 서비스에 위임
                webSocketAuthService.authorize(authentication, accessor.getDestination());
            }
        }

        return message;
    }

    private Authentication authenticate(StompHeaderAccessor accessor) {
        Optional<String> jwtOpt = Optional.ofNullable(accessor.getFirstNativeHeader("Authorization"))
                .filter(StringUtils::hasText)
                .filter(header -> header.startsWith("Bearer "))
                .map(header -> header.substring(7));

        if (jwtOpt.isPresent() && tokenProvider.validateJwtToken(jwtOpt.get())) {
            Authentication authentication = tokenProvider.getAuthentication(jwtOpt.get());
            accessor.setUser(authentication); // 메시지에 인증 정보 설정
            return authentication;
        }
        return null;
    }
}