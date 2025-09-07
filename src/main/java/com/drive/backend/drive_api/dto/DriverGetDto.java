package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Status;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverGetDto {
    // private BigDecimal avgDrivingScore;
    private Long driverId;
    private String driverName;
    private String phoneNumber;
    private String licenseNumber;
    private Integer operatorId;
    private Integer careerYears;
    private String grade;
    private Status status;
    private String driverImagePath;

    // Entity를 DTO로 변환하는 생성자
    public DriverGetDto(Driver driver) {
        this.driverId = driver.getDriverId();
        this.driverName = driver.getDriverName();
        this.phoneNumber = driver.getPhoneNumber();
        this.licenseNumber = driver.getLicenseNumber();
        this.operatorId = driver.getOperatorId();
        this.careerYears = driver.getCareerYears();
        this.grade = driver.getGrade();
//        this.avgDrivingScore = driver.getAvgDrivingScore();
        this.status = driver.getStatus();
        this.driverImagePath = driver.getDriverImagePath();
    }
}