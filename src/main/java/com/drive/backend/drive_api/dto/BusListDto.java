package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus_old;
import com.drive.backend.drive_api.enums.FuelType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusListDto {

    private String routeNumber;
    private String routeType;
    private Integer capacity;
    private String vehicleNumber;
    private String vehicleType;
    private Integer vehicleYear;
    private LocalDate lastMaintenance;
    private Integer repairCount;
    private FuelType fuelType;

    private BusListDto(Bus_old busOld) {
        this.routeNumber = busOld.getRouteNumber();
        this.routeType = busOld.getRouteType();
        this.capacity = busOld.getCapacity();
        this.vehicleNumber = busOld.getVehicleNumber();
        this.vehicleType = busOld.getVehicleType();
        this.vehicleYear = busOld.getVehicleYear();
        this.lastMaintenance = busOld.getLastMaintenance();
        this.repairCount = busOld.getRepairCount();
        this.fuelType = busOld.getFuelType();
    }

    public static BusListDto from(Bus_old busOld) {
        return new BusListDto(busOld);
    }

}
