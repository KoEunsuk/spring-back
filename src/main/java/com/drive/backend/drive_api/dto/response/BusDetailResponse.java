package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.enums.FuelType;
import com.drive.backend.drive_api.enums.RouteType;
import com.drive.backend.drive_api.enums.VehicleType;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusDetailResponse {
    private final Long busId;
    private final String routeNumber;
    private final RouteType routeType;
    private final Integer capacity;
    private final String vehicleNumber;
    private final VehicleType vehicleType;
    private final Integer vehicleYear;
    private final FuelType fuelType;
    private final LocalDate lastMaintenance;
    private final Integer repairCount;
    private final String operatorName;

    private BusDetailResponse(Long busId, String routeNumber, RouteType routeType, Integer capacity, String vehicleNumber, VehicleType vehicleType, Integer vehicleYear, FuelType fuelType, LocalDate lastMaintenance, Integer repairCount, String operatorName) {
        this.busId = busId;
        this.routeNumber = routeNumber;
        this.routeType = routeType;
        this.capacity = capacity;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.vehicleYear = vehicleYear;
        this.fuelType = fuelType;
        this.lastMaintenance = lastMaintenance;
        this.repairCount = repairCount;
        this.operatorName = operatorName;
    }

    public static BusDetailResponse from(Bus bus) {
        return new BusDetailResponse(
                bus.getBusId(),
                bus.getRouteNumber(),
                bus.getRouteType(),
                bus.getCapacity(),
                bus.getVehicleNumber(),
                bus.getVehicleType(),
                bus.getVehicleYear(),
                bus.getFuelType(),
                bus.getLastMaintenance(),
                bus.getRepairCount(),
                bus.getOperator().getOperatorName()
                );
    }
}
