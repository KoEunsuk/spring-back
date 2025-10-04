package com.drive.backend.drive_api.controller.rest;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.LoginRequest;
import com.drive.backend.drive_api.dto.request.SignupRequest;
import com.drive.backend.drive_api.dto.response.JwtResponse;
import com.drive.backend.drive_api.dto.response.SignupResponse;
import com.drive.backend.drive_api.security.jwt.JwtTokenProvider;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import com.drive.backend.drive_api.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

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
    public ResponseEntity<ApiResponse<JwtResponse>> login(@Valid @RequestBody LoginRequest loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtTokenProvider.generateJwtToken(userDetails);
        JwtResponse responseDto = new JwtResponse(jwt, userDetails);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", responseDto));
    }
}