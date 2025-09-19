package com.drive.backend.drive_api.dto;

import lombok.Getter;

@Getter
public class PasswordChangeDto {

    private String currentPassword;
    private String newPassword;
}
