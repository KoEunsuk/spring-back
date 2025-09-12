package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.enums.FuelType;
import lombok.*;

import java.math.BigDecimal;
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

    private FuelType fuelType;

    // 운전자 정보를 별도의 객체로 묶어서 관리
    private DriverSimpleDto currentDriver;

    private Long totalMileage;
    private BigDecimal averageFuelEfficiency;


    @Getter
    public static class DriverSimpleDto {
        private Long driverId;
        private String driverName;

        private DriverSimpleDto(Long driverId, String driverName) {
            this.driverId = driverId;
            this.driverName = driverName;
        }

        public static DriverSimpleDto from(Driver driver) {
            return new DriverSimpleDto(driver.getDriverId(), driver.getDriverName());
        }
    }

    private BusDto(Bus bus) {
        this.busId = bus.getBusId();
        this.routeNumber = bus.getRouteNumber();
        this.routeType = bus.getRouteType();
        this.capacity = bus.getCapacity();
        this.vehicleNumber = bus.getVehicleNumber();
        this.vehicleType = bus.getVehicleType();
        this.vehicleYear = bus.getVehicleYear();
        this.lastMaintenance = bus.getLastMaintenance();
        this.repairCount = bus.getRepairCount();
        this.operatorId = bus.getOperator().getOperatorId();
        this.fuelType = bus.getFuelType();
        this.totalMileage = bus.getTotalMileage();
        this.averageFuelEfficiency = bus.getAverageFuelEfficiency();

        this.currentDriver = (bus.getDriver() != null)
                ? DriverSimpleDto.from(bus.getDriver())
                : null;
    }

    public static BusDto from(Bus bus) {
        return new BusDto(bus);
    }

}