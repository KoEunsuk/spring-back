package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverStatusDto {
    private Long id;
    private String name;
    private String status;   // 운행 중, 대기
    private double latitude; // 위도
    private double longitude; // 경도
}