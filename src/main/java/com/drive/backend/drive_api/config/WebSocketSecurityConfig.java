package com.drive.backend.drive_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.SimpMessageType;
import org.springframework.security.config.annotation.web.messaging.MessageSecurityMetadataSourceRegistry;
import org.springframework.security.config.annotation.web.socket.AbstractSecurityWebSocketMessageBrokerConfigurer;

@Configuration
public class WebSocketSecurityConfig extends AbstractSecurityWebSocketMessageBrokerConfigurer {

    @Override
    protected void configureInbound(MessageSecurityMetadataSourceRegistry messages) {
        messages
                // 1. CONNECT 요청은 인증만 되면 허용 (StompHandler에서 인증 처리)
                //          만약 StompHandler에서 인증 실패 시 여기서 막히게 됨
                .simpTypeMatchers(SimpMessageType.CONNECT).authenticated()

                // 2. `/app/drive-events` 경로로 메시지를 보내는 것은 DRIVER 역할만 가능
                .simpDestMatchers("/app/drive-events").hasRole("DRIVER")

                // 3. 관리자가 경고를 구독하는 `/topic/operator/**` 경로는 ADMIN 역할만 가능
                .simpSubscribeDestMatchers("/topic/operator/**").hasRole("ADMIN")

                // 4. 그 외 다른 모든 메시지 발행 및 구독은 인증만 되면 허용
                .simpTypeMatchers(SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).authenticated()

                // 5. 명시적으로 허용하지 않은 다른 모든 것은 거부
                .anyMessage().denyAll();
    }

    /**
     * CSRF 토큰 검사를 웹소켓에 대해서는 비활성화합니다.
     */
    @Override
    protected boolean sameOriginDisabled() {
        return true;
    }
}