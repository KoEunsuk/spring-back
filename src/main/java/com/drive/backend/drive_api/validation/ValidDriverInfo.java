package com.drive.backend.drive_api.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = DriverInfoValidator.class)
@Target({ ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidDriverInfo {

    String message() default "운전자(DRIVER) 역할에 필요한 정보가 누락되었습니다.";

    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}