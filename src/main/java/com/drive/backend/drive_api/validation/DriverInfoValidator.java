package com.drive.backend.drive_api.validation;

import com.drive.backend.drive_api.dto.request.SignupRequest;
import com.drive.backend.drive_api.enums.Role;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.springframework.util.StringUtils;

public class DriverInfoValidator implements ConstraintValidator<ValidDriverInfo, SignupRequest> {

    @Override
    public boolean isValid(SignupRequest dto, ConstraintValidatorContext context) {
        if (dto.getRole() != Role.DRIVER) {
            return true; // 역할이 DRIVER가 아니면 검사할 필요 없음
        }

        // licenseNumber Null 검사
        if (!StringUtils.hasText(dto.getLicenseNumber())) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("운전자 역할은 면허 번호가 필수입니다.")
                    .addPropertyNode("licenseNumber")
                    .addConstraintViolation();
            return false; // 검증 실패
        }
        
        return true; // 검증 성공
    }
}