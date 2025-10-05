package com.drive.backend.drive_api.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;

@Getter
public class RefreshTokenRequest {

    @NotBlank(message = "리프레시 토큰은 비어 있을 수 없습니다.")
    private String refreshToken;

}
