package com.drive.backend.drive_api.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // 1. 메시지 구독 요청의 prefix 설정
        //      클라이언트가 "/topic/..."(1:N)이나 "/queue/..."(1:1) 경로로 구독 신청
        registry.enableSimpleBroker("/topic", "/queue");

        // 2. 메시지 발행(송신) 요청의 prefix 설정
        //      클라이언트가 "/app/..." 경로로 메시지를 보내면 서버의 @MessageMapping 메서드로 라우팅
        registry.setApplicationDestinationPrefixes("/app");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // 3. 웹소켓 연결을 위한 최초 접속 엔드포인트 설정
        //      클라이언트는 "/ws" 경로로 웹소켓 연결을 시작함
        registry.addEndpoint("/ws")
                .setAllowedOrigins("http://localhost:3000")  // CORS 설정
                .withSockJS();  // SockJS 지원
    }

}
