package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.AdminCreateRequest;
import com.drive.backend.drive_api.dto.AdminResponseDto;
import com.drive.backend.drive_api.service.AdminService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admins") // Admin 관련 API 경로
// @CrossOrigin // WebConfig에서 전역 CORS 설정 시 제거
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // 새로운 Admin 프로필 생성 (User 계정 생성 포함)
    @PostMapping
    public ResponseEntity<AdminResponseDto> createAdminProfile(@RequestBody AdminCreateRequest request) {
        AdminResponseDto response = adminService.createAdminProfile(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Admin 프로필 ID로 조회
    // GET /api/admins/{id}
    @GetMapping("/{id}")
    public ResponseEntity<AdminResponseDto> getAdminProfileById(@PathVariable Long id) {
        AdminResponseDto response = adminService.getAdminById(id);
        return ResponseEntity.ok(response);
    }

    // 이 외에 Admin 목록 조회, 수정, 삭제 등의 API 추가 가능
}