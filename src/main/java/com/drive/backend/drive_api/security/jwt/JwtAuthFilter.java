package com.drive.backend.drive_api.security.jwt;

import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class JwtAuthFilter extends OncePerRequestFilter {

    private final JwtTokenProvider jwtTokenProvider;

    private static final Logger logger = LoggerFactory.getLogger(JwtAuthFilter.class);

    public JwtAuthFilter(JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        try {
            // HTTP 요청에서 JWT 토큰 추출
            String jwt = parseJwt(request);

            // JWT 토큰이 존재하고 유효한지 검증
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateJwtToken(jwt)) {
                // 1. Provider가 인증 객체 '생성' (내부적으로 UserDetails 조회 및 비밀번호 변경 검증까지 모두 처리)
                Authentication authentication = jwtTokenProvider.getAuthentication(jwt);

                // (선택) 웹 요청 상세 정보(IP 주소 등)를 추가. 이 로직은 request 객체가 있는 Filter에 남겨두는 것이 자연스러움.
                if (authentication instanceof UsernamePasswordAuthenticationToken authToken) {
                    authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                }
                // 2. Filter가 생성된 인증 객체를 SecurityContext에 '등록' (필수!)
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (JwtException e) {
            // [수정] 비밀번호 변경으로 인한 예외 등 JWT 관련 예외 처리
            logger.error("JWT 인증 처리 중 오류 발생: {}", e.getMessage());
            SecurityContextHolder.clearContext(); // 컨텍스트를 깨끗하게 비움
        } catch (Exception e) {
            logger.error("사용자 인증을 설정할 수 없습니다: {}", e.getMessage());
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