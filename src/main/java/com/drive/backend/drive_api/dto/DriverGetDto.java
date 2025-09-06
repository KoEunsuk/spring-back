package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Driver;
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
    private String grade;
    private Integer careerYears;
    private Long driverId;
    private String driverName;
    private String phoneNumber;
    private String licenseNumber;
    private Integer operatorId;
    private Integer careerYear;
    private String Grade;
    private String status;
    private String driverImagePath;

    // Entity를 DTO로 변환하는 생성자
    public DriverGetDto(Driver driver) {
        this.driverId = driver.getDriverId();
        this.driverName = driver.getDriverName();
        this.phoneNumber = driver.getPhoneNumber();
        this.licenseNumber = driver.getLicenseNumber();
        this.careerYears = driver.getCareerYears();
//        this.avgDrivingScore = driver.getAvgDrivingScore();
        this.grade = driver.getGrade();
        if (driver.getStatus() != null) {
            this.status = driver.getStatus().getValue(); // Enum의 한글 값을 String으로 변환
        }
        this.driverImagePath = driver.getDriverImagePath();
    }
}