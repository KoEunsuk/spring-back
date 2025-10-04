package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.PasswordChangeRequest;
import com.drive.backend.drive_api.dto.request.UserUpdateRequest;
import com.drive.backend.drive_api.dto.response.UserDetailResponse;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.UserRepository;
import com.drive.backend.drive_api.security.SecurityUtil;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserDetailResponse getMyProfile() {
        return UserDetailResponse.from(getCurrentUserEntity());
    }

    @Transactional
    public UserDetailResponse updateMyProfile(UserUpdateRequest updateDto) {
        User currentUser = getCurrentUserEntity();
        // 공통 정보 업데이트
        if (updateDto.getPhoneNumber() != null) currentUser.setPhoneNumber(updateDto.getPhoneNumber());

        // 역할별 업데이트
        if (currentUser instanceof Driver driver) {
            // Driver일 경우
            if (updateDto.getLicenseNumber() != null) {
                driver.setLicenseNumber(updateDto.getLicenseNumber());
            }
        } else if (currentUser instanceof Admin admin) {
            // Admin일 경우
            
        }
        return UserDetailResponse.from(currentUser);
    }

    @Transactional
    public void changeMyPassword(PasswordChangeRequest passwordDto) {
        User currentUser = getCurrentUserEntity();

        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        currentUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

        currentUser.setPasswordChangedAt(Instant.now());
    }

    @Transactional
    public void deleteMyAccount() {
        User currentUser = getCurrentUserEntity();

        // Driver의 경우, 배차가 남아있으면 삭제 불가.
        if (currentUser instanceof Driver driver) {
            if (!driver.getDispatches().isEmpty()) {
                throw new IllegalStateException("배차 기록이 존재하여 계정을 삭제할 수 없습니다. 관리자에게 문의하세요.");
            }
        }

        userRepository.delete(currentUser);    }

    private User getCurrentUserEntity() {
        Long currentUserId = SecurityUtil.getCurrentUser()
                .map(CustomUserDetails::getUserId)
                .orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
    }
}