package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

/**
 * 이 DTO는 운전자 상세정보 페이지라고 생각하면됨, 운전자 운행기록부 + 차량 상태 점검표 느낌 근데 수정해야할듯 요소들은
 */
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