package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.JwtResponse;
import com.drive.backend.drive_api.dto.LoginRequest;
import com.drive.backend.drive_api.dto.UserDto;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "http://localhost:3000")
public class AuthController {

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(@RequestBody UserDto userDto) {
        // 회원가입 로직 (비밀번호 암호화, DB 저장 등)
        return ResponseEntity.ok("회원가입 성공");
    }

    @PostMapping("/login")
    public ResponseEntity<?> loginUser(@RequestBody LoginRequest loginRequest) {
        // 로그인 검증 후 JWT 토큰 발급 등
        return ResponseEntity.ok(new JwtResponse("jwt_token_here", loginRequest.getUsername()));
    }
}