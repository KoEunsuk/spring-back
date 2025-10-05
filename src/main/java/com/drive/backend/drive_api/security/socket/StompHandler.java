package com.drive.backend.drive_api.security.socket;

import com.drive.backend.drive_api.security.jwt.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
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
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);
        StompCommand command = accessor.getCommand();

        // 1. JWT를 이용한 인증 처리 (CONNECT 명령어에 대해서만 수행)
        if (StompCommand.CONNECT.equals(command)) {
            Optional.ofNullable(accessor.getFirstNativeHeader("Authorization"))
                    .filter(StringUtils::hasText)
                    .filter(header -> header.startsWith("Bearer "))
                    .map(header -> header.substring(7))
                    .ifPresent(token -> {
                        if (tokenProvider.validateToken(token)) {
                            Authentication authentication = tokenProvider.getAuthentication(token);
                            accessor.setUser(authentication); // 인증 정보를 세션에 저장
                        }
                    });
        }

        // 2. 인가 처리 (SUBSCRIBE, SEND 명령어에 대해서만 수행)
        if (StompCommand.SUBSCRIBE.equals(command) || StompCommand.SEND.equals(command)) {
            Authentication authentication = (Authentication) accessor.getUser();
            // 인가 처리를 서비스에 위임
            webSocketAuthService.authorize(authentication, accessor.getDestination());
        }

        return message;
    }
}