package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;

@Getter
public class LoginResponse {
    private final String accessToken;
    private final String refreshToken;  // 앱 클라이언트용. 웹은 헤더의 쿠키 이용
    private final Long userId;
    private final String username;
    private final String email;
    private final List<String> roles;

    public LoginResponse(String accessToken, String refreshToken, CustomUserDetails userDetails) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.userId = userDetails.getUserId();
        this.username = userDetails.getRealUsername();
        this.email = userDetails.getEmail();
        this.roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .toList();
    }
}
