package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Grade;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;

@Entity
@Table(name = "drivers")
@DiscriminatorValue("DRIVER")
@Getter @Setter @NoArgsConstructor
public class Driver extends User {

    @Column
    private String licenseNumber;

    @Column
    private Integer careerYears;

    @Enumerated(EnumType.STRING)
    @Column
    private Grade grade;

    @Column(precision = 5, scale = 2)
    private BigDecimal avgDrowsinessCount;

    @Column(precision = 5, scale = 2)
    private BigDecimal avgAccelerationCount;

    @Column(precision = 5, scale = 2)
    private BigDecimal avgBrakingCount;

    @Column(precision = 5, scale = 2)
    private BigDecimal avgAbnormalCount;

    @Column(precision = 5, scale = 2)
    private BigDecimal avgDrivingScore;
}
