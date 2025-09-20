package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.Grade;
import lombok.Getter;

@Getter
public class DriverAdminUpdateRequestDto {
    private String phoneNumber;

    private String licenseNumber;
    private Integer careerYears;
    private Grade grade;
}
