package com.drive.backend.drive_api.security.userdetails;

import com.drive.backend.drive_api.entity.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
public class CustomUserDetails implements UserDetails {

    private Long id;
    private Long operatorId;

    private String username;
    @JsonIgnore
    private String password;
    private Collection<? extends GrantedAuthority> authorities;

    public static CustomUserDetails build(User user) {
        List<GrantedAuthority> authorities = user.getRole() != null ?
                List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())) :
                List.of();

        return new CustomUserDetails(
                user.getId(),
                user.getOperator() != null ? user.getOperator().getOperatorId() : null,
                user.getUsername(),
                user.getPassword(),
                authorities);
    }

    private CustomUserDetails(Long id, Long operatorId, String username, String password, //
                              Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.operatorId = operatorId; //
        this.username = username;
        this.password = password;
        this.authorities = authorities;
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
        return Objects.equals(id, that.id);
    }
}