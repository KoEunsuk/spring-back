package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.JwtResponse;
import com.drive.backend.drive_api.dto.LoginRequest;
import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.service.AuthService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
// @CrossOrigin(origins = "http://localhost:3000") // WebConfig에서 전역 CORS 설정 시 제거 가능
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<String> registerUser(@RequestBody UserDto userDto) {
        authService.registerUser(userDto);
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<JwtResponse> loginUser(@RequestBody LoginRequest loginRequest) {
        JwtResponse jwtResponse = authService.authenticateUser(loginRequest);
        return ResponseEntity.ok(jwtResponse);
    }
}