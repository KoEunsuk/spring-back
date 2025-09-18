package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.net.URI;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    // 회원가입 API
    @PostMapping("/signup")
    public ResponseEntity<Void> signup(@RequestBody SignupRequestDto signupDto) {
        authService.signup(signupDto);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }
}