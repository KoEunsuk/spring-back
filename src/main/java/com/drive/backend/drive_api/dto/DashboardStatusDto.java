package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatusDto {
    private long totalRides;        // 총 운행 횟수
    private long totalDrivers;      // 운전자 수
    private double avgSatisfaction; // 평균 만족도
    private long todayAlerts;       // 금일 알림 건수
}
