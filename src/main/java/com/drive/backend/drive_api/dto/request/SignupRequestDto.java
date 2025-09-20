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
    private Role role;
    //공통이지만 널허용
    private String imagePath;

    private String licenseNumber;
    private Integer careerYears;
}
