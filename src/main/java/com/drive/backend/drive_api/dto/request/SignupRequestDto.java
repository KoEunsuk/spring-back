package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.validation.ValidDriverInfo;
import jakarta.validation.constraints.*;
import lombok.Getter;

@Getter
@ValidDriverInfo    // 운전자 정보(면허, 경력) 필수 여부 검사
public class SignupRequestDto {

    @NotBlank(message = "이메일은 필수 입력 항목입니다.")
    @Email(message = "유효한 이메일 형식이 아닙니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수 입력 항목입니다.")
    @Size(min = 8, message = "비밀번호는 최소 8자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "사용자 이름은 필수 입력 항목입니다.")
    private String username;

    @NotBlank(message = "운수사 코드는 필수 입력 항목입니다.")
    private String operatorCode;

    @NotBlank(message = "전화번호는 필수 입력 항목입니다.")
    @Pattern(regexp = "^\\d{3}-\\d{3,4}-\\d{4}$", message = "유효한 전화번호 형식이 아닙니다. (예: 010-1234-5678)")
    private String phoneNumber;

    @NotNull(message = "역할(Role)은 필수 선택 항목입니다.")
    private Role role;

    //공통이지만 널허용
    private String imagePath;

    // 운전자 전용 필드 -> 커스텀 Validator에서 검사
    private String licenseNumber;

    @PositiveOrZero(message = "경력 연수는 0 이상의 숫자여야 합니다.")
    private Integer careerYears;
}
