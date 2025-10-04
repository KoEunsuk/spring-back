package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;

import java.util.List;
import java.util.stream.Collectors;

@Getter
public class JwtResponse {
    private final String token;
    private final Long userId;
    private final String email;
    private final String username;
    private final List<String> roles;

    public JwtResponse(String token, CustomUserDetails userDetails) {
        this.token = token;
        this.userId = userDetails.getUserId();
        this.email = userDetails.getEmail();
        this.username = userDetails.getRealUsername();
        this.roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());
    }
}
