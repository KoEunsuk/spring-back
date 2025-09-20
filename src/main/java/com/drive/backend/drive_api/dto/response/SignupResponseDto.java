package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import lombok.Getter;

@Getter
public class SignupResponseDto {
    private final Long userId;
    private final String email;
    private final String username;
    private final String phoneNumber;
    private final String imagePath;
    private final Role role;
    private final Long operatorId;

    private SignupResponseDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.imagePath = user.getImagePath();
        this.role = user.getRole();
        Operator operator = user.getOperator();
        if (operator != null) {
            this.operatorId = operator.getOperatorId();
        } else {
            this.operatorId = null;
        }
    }

    public static SignupResponseDto from(User user) {
        return new SignupResponseDto(user);
    }
}