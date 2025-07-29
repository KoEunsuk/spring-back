package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverDetailsDto {
    private Long id;
    private String name;
    private String carModel;  // 차량 모델
    private int speed;        // 현재 속도 (km/h)
    private int rpm;          // 엔진 RPM
    private int fuelLevel;    // 연료량 (%)
}