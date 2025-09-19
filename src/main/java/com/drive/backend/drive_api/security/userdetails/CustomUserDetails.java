package com.drive.backend.drive_api.security.userdetails;

import com.drive.backend.drive_api.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.Instant;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

@Getter
public class CustomUserDetails implements UserDetails {

    private final Long userId;
    private final String email;
    private final String realUsername;
    private final Long operatorId;
    private final Instant passwordChangedAt;

    @JsonIgnore
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails build(User user) {
        List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name()));

        return new CustomUserDetails(
                user.getUserId(),
                user.getEmail(),
                user.getUsername(),
                user.getOperator() != null ? user.getOperator().getOperatorId() : null,
                user.getPasswordChangedAt(),
                user.getPassword(),
                authorities);
    }

    private CustomUserDetails(Long userId, String email, String realUsername, Long operatorId,
                              Instant passwordChangedAt, String password,
                              Collection<? extends GrantedAuthority> authorities) {
        this.userId = userId;
        this.email = email;
        this.realUsername = realUsername;
        this.operatorId = operatorId;
        this.passwordChangedAt = passwordChangedAt;
        this.password = password;
        this.authorities = authorities;
    }

    @Override
    public String getUsername() {
        // Spring Security가 'username'으로 인식할 고유 식별자로 email을 반환
        return this.email;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }


    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }


    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CustomUserDetails that = (CustomUserDetails) o;
        return Objects.equals(userId, that.userId);
    }
}