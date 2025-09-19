package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.PasswordChangeDto;
import com.drive.backend.drive_api.dto.UserDetailDto;
import com.drive.backend.drive_api.dto.UserUpdateDto;
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

    public UserDetailDto getMyProfile() {
        return UserDetailDto.from(getCurrentUserEntity());
    }

    @Transactional
    public UserDetailDto updateMyProfile(UserUpdateDto updateDto) {
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
        return UserDetailDto.from(currentUser);
    }

    @Transactional
    public void changeMyPassword(PasswordChangeDto passwordDto) {
        User currentUser = getCurrentUserEntity();

        if (!passwordEncoder.matches(passwordDto.getCurrentPassword(), currentUser.getPassword())) {
            throw new IllegalArgumentException("현재 비밀번호가 일치하지 않습니다.");
        }

        currentUser.setPassword(passwordEncoder.encode(passwordDto.getNewPassword()));

        currentUser.setPasswordChangedAt(Instant.now());
    }

    @Transactional
    public void deleteMyAccount() {
        userRepository.delete(getCurrentUserEntity());
    }

    private User getCurrentUserEntity() {
        Long currentUserId = SecurityUtil.getCurrentUser()
                .map(CustomUserDetails::getUserId)
                .orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

        return userRepository.findById(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User", "id", currentUserId));
    }
}