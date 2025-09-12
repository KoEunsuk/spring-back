package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusUpdateRequestDto {

    private String routeNumber;
    private Integer capacity;
    private LocalDate lastMaintenance;
    private Integer repairCount;

}
