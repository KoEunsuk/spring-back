package com.drive.backend.drive_api.dto.request;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class UserUpdateRequest {
    // 공통 정보
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String phoneNumber;

    // 운전자
    @Size(min = 1, message = "면허 번호는 비워둘 수 없습니다.")
    private String licenseNumber;

    // 관리자

}
