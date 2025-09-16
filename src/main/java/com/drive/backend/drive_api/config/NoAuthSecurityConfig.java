package com.drive.backend.drive_api.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile; // ⭐ Profile 임포트 ⭐
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder; // PasswordEncoder 빈은 그대로 유지
import org.springframework.security.crypto.password.PasswordEncoder; // PasswordEncoder 빈은 그대로 유지
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@Profile("no-auth")
public class NoAuthSecurityConfig {

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //  "테스트 하려고 인증 제거"
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable) // 람다 방식으로 disable
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll()); // ⭐ 모든 요청 허용! ⭐

        return http.build();
    }
}