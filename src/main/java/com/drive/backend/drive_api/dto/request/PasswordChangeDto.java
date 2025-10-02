package com.drive.backend.drive_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
public class PasswordChangeDto {
    @NotBlank(message = "현재 비밀번호를 입력해주세요.")
    private String currentPassword;

    @NotBlank(message = "새로운 비밀번호를 입력해주세요.")
    @Size(min = 8, message = "새로운 비밀번호는 최소 8자 이상이어야 합니다.")
    private String newPassword;
}
