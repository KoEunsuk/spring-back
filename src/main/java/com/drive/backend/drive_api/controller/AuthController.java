package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.JwtResponse;
import com.drive.backend.drive_api.dto.LoginRequest;
import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.dto.UserResponseDto;
import com.drive.backend.drive_api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 일반 사용자 회원가입 API
    // POST /api/auth/register
    @PostMapping("/register")
    public ResponseEntity<UserResponseDto> registerUser(@RequestBody UserDto userDto) {
        UserResponseDto response = authService.registerUser(userDto);
        return ResponseEntity.ok(response);
    }

    // 관리자 계정 생성 API
    @PostMapping("/register-admin")
    public ResponseEntity<UserResponseDto> registerAdmin(@RequestBody UserDto adminDto) {
        UserResponseDto response = authService.registerAdmin(adminDto);
        return ResponseEntity.ok(response);
    }

    // 사용자 로그인 API
    // POST /api/auth/login
    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}