package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import lombok.Getter;

@Getter
public class LoginResponseDto {
    private final Long userId;
    private final String email;
    private final String username;
    private final String phoneNumber;
    private final String imagePath;
    private final Role role;
    private final Long operatorId;

    private LoginResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.imagePath = user.getImagePath();
        this.role = user.getRole();
        this.operatorId = user.getOperator().getOperatorId();
    }

    public static LoginResponseDto from(User user) {
        return new LoginResponseDto(user);
    }
}