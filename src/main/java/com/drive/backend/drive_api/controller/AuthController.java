package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.security.jwt.JwtTokenProvider;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import com.drive.backend.drive_api.service.AuthService;
import com.drive.backend.drive_api.dto.SignupResponseDto;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    public AuthController(AuthService authService, AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
        this.authService = authService;
        this.authenticationManager = authenticationManager;
        this.jwtTokenProvider = jwtTokenProvider;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<ApiResponse<SignupResponseDto>> signup(@Valid @RequestBody SignupRequestDto signupDto) {
        SignupResponseDto createdUser = authService.signup(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ApiResponse.success("회원가입이 성공적으로 완료되었습니다.", createdUser));
    }

    // 로그인 API
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<JwtResponseDto>> login(@Valid @RequestBody LoginRequestDto loginDto) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));

        SecurityContextHolder.getContext().setAuthentication(authentication);

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

        String jwt = jwtTokenProvider.generateJwtToken(userDetails);
        JwtResponseDto responseDto = new JwtResponseDto(jwt, userDetails);
        return ResponseEntity.ok(ApiResponse.success("로그인 성공", responseDto));
    }
}