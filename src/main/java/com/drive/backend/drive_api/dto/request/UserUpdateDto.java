package com.drive.backend.drive_api.dto.request;

import lombok.Getter;

@Getter
public class UserUpdateDto {
    // 공통 정보
    private String phoneNumber;

    // 운전자
    private String licenseNumber;

    // 관리자

}
