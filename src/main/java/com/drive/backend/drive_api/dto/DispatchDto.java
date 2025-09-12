package com.drive.backend.drive_api.dto;


import com.drive.backend.drive_api.enums.DispatchStatus;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class DispatchDto {
    private Long dispatchId; // 배차 ID
    private Long driverId;   // 운전자의 ID
    private Long busId;      // 버스의 ID
    private DispatchStatus status;   // 배차 상태
    private LocalDate dispatchDate; // 배차 날짜
    private LocalTime scheduledDeparture; // 예정 출발 시간
    private LocalTime actualDeparture;    // 실제 출발 시간
    private LocalTime actualArrival;      // 실제 도착 시간
    private Integer warningCount; // 경고 횟수
    private Integer drivingScore; // 운전 점수


    private String driverName; // 운전자의 이름
    private String busVehicleNumber; // 할당된 버스의 차량 번호
}