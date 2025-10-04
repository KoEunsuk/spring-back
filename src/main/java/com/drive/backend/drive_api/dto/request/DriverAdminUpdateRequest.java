package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.Grade;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class DriverAdminUpdateRequest {

    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String phoneNumber;

    @Size(min = 1, message = "면허 번호는 비워둘 수 없습니다.")
    private String licenseNumber;

    @PositiveOrZero(message = "경력 연수는 0 이상의 숫자여야 합니다.")
    private Integer careerYears;

    private Grade grade;
}
