package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Grade;
import com.drive.backend.drive_api.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "drivers")
@DiscriminatorValue("DRIVER")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Driver extends User {

    @Column(nullable = false)
    private String licenseNumber;

    @Column
    private Integer careerYears;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Grade grade = Grade.E;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal avgDrowsinessCount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal avgAccelerationCount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal avgBrakingCount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal avgAbnormalCount = BigDecimal.ZERO;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal avgDrivingScore = new BigDecimal("100.00");

    @OneToMany(mappedBy = "driver")
    private List<Dispatch> dispatches = new ArrayList<>();

    // 필수값 생성자
    public Driver(String email, String password, String username, String phoneNumber, Operator operator, String licenseNumber) {
        super(email, password, username, phoneNumber, operator);
        this.licenseNumber = licenseNumber;
    }

    @Override
    public Role getRole() {
        return Role.DRIVER;
    }

    // 연관관계 편의 메서드
    public void addDispatch(Dispatch dispatch) {
        this.dispatches.add(dispatch);
        if (dispatch.getDriver() != this) {
            dispatch.setDriver(this);
        }
    }
}
