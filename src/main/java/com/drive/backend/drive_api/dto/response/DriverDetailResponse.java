package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.enums.Grade;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
public class DriverDetailResponse {
    private final Long userId;
    private final String username;
    private final String phoneNumber;
    private final String imagePath;
    private final String licenseNumber;
    private final Integer careerYears;
    private final Grade grade;
    private final BigDecimal avgDrivingScore;

    private DriverDetailResponse(Long userId, String username, String phoneNumber, String imagePath,
                                 String licenseNumber, Integer careerYears, Grade grade, BigDecimal avgDrivingScore) {
        this.userId = userId;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.imagePath = imagePath;
        this.licenseNumber = licenseNumber;
        this.careerYears = careerYears;
        this.grade = grade;
        this.avgDrivingScore = avgDrivingScore;
    }

    public static DriverDetailResponse from(Driver driver) {
        // private 생성자를 호출하여 DTO 객체를 생성하고 반환
        return new DriverDetailResponse(
                driver.getUserId(),
                driver.getUsername(),
                driver.getPhoneNumber(),
                driver.getImagePath(),
                driver.getLicenseNumber(),
                driver.getCareerYears(),
                driver.getGrade(),
                driver.getAvgDrivingScore()
        );
    }
}
