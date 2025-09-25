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
                // 1. `/app/drive-events` 경로로 메시지를 '보내는(destination)' 것은 DRIVER 역할만 가능
                .simpDestMatchers("/app/drive-events").hasRole("DRIVER")

                // 2. 관리자가 경고를 '구독하는(subscribe)' `/topic/operator/**` 경로는 ADMIN 역할만 가능
                .simpSubscribeDestMatchers("/topic/operator/**").hasRole("ADMIN")

                // 3. 웹소켓 연결 자체(CONNECT)나 단순 메시지(MESSAGE)는 인증만 되면 허용
                //    (위에서 지정한 특정 경로 외의 일반적인 통신)
                .simpTypeMatchers(SimpMessageType.CONNECT, SimpMessageType.MESSAGE, SimpMessageType.SUBSCRIBE).authenticated()

                // 4. 명시적으로 허용하지 않은 다른 모든 것은 거부하여 보안 강화
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