package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ScheduleDto {
    private Long id;
    private String vehicleNumber;   // 차량 번호
    private String driverName;      // 운전자 이름
    private LocalDateTime startTime; // 출발 시간
    private LocalDateTime endTime;   // 도착 시간
    private boolean isCompleted;    // 운행 완료 여부
}