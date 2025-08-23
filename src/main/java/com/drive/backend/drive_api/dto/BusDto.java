package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class BusDto {

    private Long busId; // 버스 고유 ID값.
    private String routeNumber; // 버스 노선번호값.
    private String routeType; // 버스 타입 -> 뭐 광역인지 마을인지.
    private Integer capacity; // 버스 정원.
    private String vehicleNumber; // 버스 차량 번호.
    private String vehicleType; // 버스 차량 종류.
    private Integer vehicleYear; // 연식
    private LocalDate lastMaintenance; // 마지막 정비일.
    private Integer repairCount; // 정비 수리 횟수.

    private Long operatorId; // 버스가 속한 회사의 ID값.
    private String operatorName; // 해당 회사 이름.
}