package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.Role;
import lombok.Getter;

@Getter
public class SignupRequestDto {
    private String email;
    private String password;
    private String username;
    private String operatorCode;
    private String phoneNumber;
    private String imagePath;
    private Role role;

    private String licenseNumber;
    private Integer careerYears;
}
