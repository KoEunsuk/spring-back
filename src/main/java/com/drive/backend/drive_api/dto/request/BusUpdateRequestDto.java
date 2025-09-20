package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.RouteType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusUpdateRequestDto {
    private String routeNumber;
    private RouteType routeType;
    private LocalDate lastMaintenance;
    private Integer capacity;
    private String vehicleNumber;
    private Integer repairCount;
}
