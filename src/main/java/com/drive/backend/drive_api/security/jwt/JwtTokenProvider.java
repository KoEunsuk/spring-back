package com.drive.backend.drive_api.security.jwt;

import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JwtTokenProvider { // 토큰의 생성, 검증 담당

    private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);
    private final SecretKey key;

    public JwtTokenProvider(@Value("${jwt.secret}") String secret) {
        // Base64 디코딩 대신, 문자열을 바로 키로 변환
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    @Value("${app.jwtExpirationMs}") // 프로퍼티에서 값 획득
    private int jwtExpirationMs;

    // 토큰 생성
    public String generateJwtToken(CustomUserDetails userDetails) {
        List<String> roles = userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList());

        return Jwts.builder()
                .setSubject((userDetails.getUsername()))
                .claim("userId", userDetails.getUserId())
                .claim("operatorId", userDetails.getOperatorId())
                .claim("roles", roles)
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(this.key, SignatureAlgorithm.HS512)
                .compact();
    }

    // JWT 토큰에서 사용자 이메일 추출
    public String getEmailFromJwtToken(String token) {
        return Jwts.parser() //Jwts.parser() 사용
                .verifyWith(this.key) // setSigningKey() 대신 verifyWith() 사용
                .build() // parser() 메서드 호출 후에도 build()를 호출해야 최종 JwtParser 객체를 얻을 수 있음
                .parseSignedClaims(token) // parseClaimsJws() 대신 parseSignedClaims() 사용
                .getPayload().getSubject(); // getBody() 대신 getPayload()를 통해 Claims 객체 접근
    }

    // 토큰 유효성 검증
    public boolean validateJwtToken(String authToken) {
        try {
            Jwts.parser()
                    .verifyWith(this.key)
                    .build() //
                    .parseSignedClaims(authToken);
            return true;
        } catch (SecurityException e) {
            logger.error("Invalid JWT signature: {}", e.getMessage());
        } catch (MalformedJwtException e) {
            logger.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            logger.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            logger.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            logger.error("JWT claims string is empty: {}", e.getMessage());
        }
        return false;
    }
}