package com.drive.backend.drive_api.controller.rest;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.PasswordChangeRequest;
import com.drive.backend.drive_api.dto.request.UserUpdateRequest;
import com.drive.backend.drive_api.dto.response.UserDetailResponse;
import com.drive.backend.drive_api.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@PreAuthorize("isAuthenticated()")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    // 내 정보 조회 API
    @GetMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> getMyProfile() {
        UserDetailResponse myProfile = userService.getMyProfile();
        return ResponseEntity.ok(ApiResponse.success("내 정보 조회 성공", myProfile));
    }

    // 내 정보 수정 API
    @PatchMapping("/me")
    public ResponseEntity<ApiResponse<UserDetailResponse>> updateMyProfile(@Valid @RequestBody UserUpdateRequest updateDto) {
        UserDetailResponse updatedProfile = userService.updateMyProfile(updateDto);
        return ResponseEntity.ok(ApiResponse.success("내 정보 수정 성공", updatedProfile));
    }

    // 비밀번호 수정 전용 API
    @PatchMapping("/me/password")
    public ResponseEntity<ApiResponse<Void>> changeMyPassword(@Valid @RequestBody PasswordChangeRequest passwordDto) {
        userService.changeMyPassword(passwordDto);
        return ResponseEntity.ok(ApiResponse.success("비밀번호가 성공적으로 변경되었습니다."));
    }

    // 회원 탈퇴 API
    @DeleteMapping("/me")
    public ResponseEntity<ApiResponse<Void>> deleteMyAccount() {
        userService.deleteMyAccount();
        return ResponseEntity.ok(ApiResponse.success("회원 탈퇴가 성공적으로 처리되었습니다."));
    }
}