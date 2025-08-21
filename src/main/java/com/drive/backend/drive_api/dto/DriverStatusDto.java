package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;


/**
 * 프론트엔드 쪽에서 현재 운전자들의 라이브 상태를 조회하려 할때 사용하는 DTo임.
 */
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