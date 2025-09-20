package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.enums.Grade;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class UserDetailDto {
    // 공통 필드
    private final Long userId;
    private final String email;
    private final String username;
    private final String phoneNumber;
    private final String imagePath;
    private final Role role;
    private final Long operatorId;
    private final String operatorName;

    // 역할별 선택적 필드
    // 운전자
    private final String licenseNumber;
    private final Integer careerYears;
    private final Grade grade;
    private final BigDecimal avgDrowsinessCount;
    private final BigDecimal avgAccelerationCount;
    private final BigDecimal avgBrakingCount;
    private final BigDecimal avgAbnormalCount;
    private final BigDecimal avgDrivingScore;

    private UserDetailDto(User user) {
        this.userId = user.getUserId();
        this.email = user.getEmail();
        this.username = user.getUsername();
        this.phoneNumber = user.getPhoneNumber();
        this.imagePath = user.getImagePath();
        this.role = user.getRole();
        Operator operator = user.getOperator();
        if (operator != null) {
            this.operatorId = operator.getOperatorId();
            this.operatorName = operator.getOperatorName(); // Operator의 getName() 메서드 사용
        } else {
            this.operatorId = null;
            this.operatorName = null;
        }

        // 선택적 생성자 처리
        if (user instanceof Driver driver) {
            // user가 Driver 타입일 경우
            this.licenseNumber = driver.getLicenseNumber();
            this.careerYears = driver.getCareerYears();
            this.grade = driver.getGrade();
            this.avgDrowsinessCount = driver.getAvgDrowsinessCount();
            this.avgAccelerationCount = driver.getAvgAccelerationCount();
            this.avgBrakingCount = driver.getAvgBrakingCount();
            this.avgAbnormalCount = driver.getAvgAbnormalCount();
            this.avgDrivingScore = driver.getAvgDrivingScore();
        } else if (user instanceof Admin admin) {
            // user가 Admin 타입일 경우
            this.licenseNumber = null;
            this.careerYears = null;
            this.grade = null;
            this.avgDrowsinessCount = null;
            this.avgAccelerationCount = null;
            this.avgBrakingCount = null;
            this.avgAbnormalCount = null;
            this.avgDrivingScore = null;
        } else {
            // Driver도 Admin도 아닌 제3의 User
            this.licenseNumber = null;
            this.careerYears = null;
            this.grade = null;
            this.avgDrowsinessCount = null;
            this.avgAccelerationCount = null;
            this.avgBrakingCount = null;
            this.avgAbnormalCount = null;
            this.avgDrivingScore = null;
        }
    }

    public static UserDetailDto from(User user) {
        return new UserDetailDto(user);
    }
}
