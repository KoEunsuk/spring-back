package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.FuelType;
import com.drive.backend.drive_api.enums.RouteType;
import com.drive.backend.drive_api.enums.VehicleType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusCreateDto {
    // 선택적 필드
    private String routeNumber;
    private RouteType routeType;
    private LocalDate lastMaintenance;
    private Integer repairCount;

    // 필수 필드
    private Integer capacity;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private Integer vehicleYear;
    private FuelType fuelType;
}
