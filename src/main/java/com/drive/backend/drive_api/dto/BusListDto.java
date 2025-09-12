package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus;
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

    private BusListDto(Bus bus) {
        this.routeNumber = bus.getRouteNumber();
        this.routeType = bus.getRouteType();
        this.capacity = bus.getCapacity();
        this.vehicleNumber = bus.getVehicleNumber();
        this.vehicleType = bus.getVehicleType();
        this.vehicleYear = bus.getVehicleYear();
        this.lastMaintenance = bus.getLastMaintenance();
        this.repairCount = bus.getRepairCount();
        this.fuelType = bus.getFuelType();
    }

    public static BusListDto from(Bus bus) {
        return new BusListDto(bus);
    }

}
