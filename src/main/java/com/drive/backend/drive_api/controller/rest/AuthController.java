package com.drive.backend.drive_api.controller.rest;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.LoginRequest;
import com.drive.backend.drive_api.dto.request.RefreshTokenRequest;
import com.drive.backend.drive_api.dto.request.SignupRequest;
import com.drive.backend.drive_api.dto.response.AccessTokenResponse;
import com.drive.backend.drive_api.dto.response.LoginResponse;
import com.drive.backend.drive_api.dto.response.SignupResponse;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import com.drive.backend.drive_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponse>> signup(@Valid @RequestBody SignupRequest signupDto) {
        SignupResponse createdUser = authService.signup(signupDto);
        URI location = URI.create("/api/users/me");

        ApiResponse<SignupResponse> response = ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", createdUser);

        return ResponseEntity.created(location).body(response);
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@Valid @RequestBody LoginRequest loginDto) {
        // 1. 서비스에 로그인 위임하여 토큰 정보 받아오기
        AuthService.TokenInfo tokenInfo = authService.login(loginDto);

        // 2. (웹용) Refresh Token을 HttpOnly 쿠키로 설정
        ResponseCookie cookie = ResponseCookie.from("refresh_token", tokenInfo.refreshToken())
                .maxAge(7 * 24 * 60 * 60) // 쿠키의 유효기간: 7일
                .path("/")  // 쿠키가 유효한 경로 설정
                .secure(false)   // TODO: HTTPS를 사용하는 경우에만 쿠키를 전송하도록 true로 변경
                .sameSite("None")   // TODO: 프론트/백엔드가 같은 도메인인 경우에는 Lax나 Strict로 설정
                .httpOnly(true) // JavaScript에서 쿠키 접근 막기
                .build();

        // 3. (웹+앱 공통용) 응답 본문에 담을 DTO 생성
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        LoginResponse loginResponse = new LoginResponse(
                tokenInfo.accessToken(),
                tokenInfo.refreshToken(),
                userDetails
        );

        // 4. 최종 응답 생성: 헤더에 쿠키, 본문에 DTO
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(ApiResponse.success("로그인에 성공하였습니다.", loginResponse));
    }

    // 로그아웃
    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<Void>> logout(
            @CookieValue(name = "refresh_token", required = false) String refreshToken,
            Authentication authentication) {
        // 1. 서비스 로직을 호출하여 서버 측 토큰을 무효화합니다.
        authService.logout(refreshToken, authentication);

        // 2. 클라이언트 측의 쿠키를 삭제하기 위한 빈 쿠키를 생성합니다.
        ResponseCookie emptyCookie = ResponseCookie.from("refresh_token", "")
                .maxAge(0)
                .path("/")  // 쿠키가 유효한 경로 설정
                .httpOnly(true) // JavaScript에서 쿠키 접근 막기
                .secure(false)   // TODO: HTTPS를 사용하는 경우에만 쿠키를 전송하도록 true로 변경
                .sameSite("None")   // TODO: 프론트/백엔드가 같은 도메인인 경우에는 Lax나 Strict로 설정
                .build();

        // 3. 응답 헤더에 쿠키 삭제 설정을 담아 보냅니다.
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, emptyCookie.toString())
                .body(ApiResponse.success("성공적으로 로그아웃되었습니다.", null));
    }

    // RefreshToken으로 Access Token 재발급
    @PostMapping("/refresh")
    public ResponseEntity<ApiResponse<AccessTokenResponse>> refreshToken(
            @CookieValue(name = "refresh_token", required = false) String refreshTokenFromCookie,
            @Valid @RequestBody(required = false) RefreshTokenRequest refreshTokenFromBody) {

        // 웹에서는 쿠키, 앱에서는 Body로 Refresh Token을 받음
        String refreshTokenString = (refreshTokenFromCookie != null) ? refreshTokenFromCookie : refreshTokenFromBody.getRefreshToken();

        // 1. 서비스에 재발급 로직 위임
        String newAccessToken = authService.refreshAccessToken(refreshTokenString);

        // 2. 새로운 Access Token을 DTO에 담아 응답
        AccessTokenResponse responseDto = new AccessTokenResponse(newAccessToken);

        return ResponseEntity.ok(ApiResponse.success("Access Token이 성공적으로 재발급되었습니다.", responseDto));
    }
}