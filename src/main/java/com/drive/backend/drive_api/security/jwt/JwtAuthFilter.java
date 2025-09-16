package com.drive.backend.drive_api.security.jwt;

import com.drive.backend.drive_api.security.userdetails.CustomUserDetailsService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;
    private final CustomUserDetailsService userDetailsService;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider, CustomUserDetailsService userDetailsService) {
        this.jwtTokenProvider = jwtTokenProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // HTTP 요청에서 JWT 토큰 추출
            String jwt = parseJwt(request);

            // JWT 토큰이 존재하고 유효한지 검증
            if (jwt != null && jwtTokenProvider.validateJwtToken(jwt)) {
                // 유효한 토큰으로부터 사용자 이름(Subject) 추출
                String username = jwtTokenProvider.getUserNameFromJwtToken(jwt);

                //사용자 이름으로 UserDetails(CustomUserDetails) 로드
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                // UserDetails를 사용하여 인증 객체(Authentication) 생성
                // 패스워드는 이미 JWT 검증 과정에서 확인되었으므로 null
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                userDetails,
                                null,
                                userDetails.getAuthorities()); // UserDetails의 권한 정보 사용

                // 웹 요청 상세 정보를 인증 객체에 추가
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // SecurityContextHolder에 인증 객체 설정
                // 이렇게 함으로써 현재 요청에 대해 사용자가 인증되었음을 Spring Security에 알림
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            logger.error("Cannot set user authentication: {}", e.getMessage());
        }


        filterChain.doFilter(request, response);
    }


    private String parseJwt(HttpServletRequest request) {
        String headerAuth = request.getHeader("Authorization");


        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }
}