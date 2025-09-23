package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.enums.DispatchStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
public class DispatchDetailDto {
    private final Long dispatchId;
    private final Long busId;
    private final String vehicleNumber;
    private final Long driverId;
    private final String driverName;
    private final String routeNumber;
    private final DispatchStatus status;
    private final LocalDate dispatchDate;
    private final LocalDateTime scheduledDepartureTime;
    private final LocalDateTime scheduledArrivalTime;
    private final LocalDateTime actualDepartureTime;
    private final LocalDateTime actualArrivalTime;

    private DispatchDetailDto(Long dispatchId, Long busId, String vehicleNumber, Long driverId, String driverName, String routeNumber, DispatchStatus status, LocalDate dispatchDate, LocalDateTime scheduledDepartureTime, LocalDateTime scheduledArrivalTime, LocalDateTime actualDepartureTime, LocalDateTime actualArrivalTime) {
        this.dispatchId = dispatchId;
        this.busId = busId;
        this.vehicleNumber = vehicleNumber;
        this.driverId = driverId;
        this.driverName = driverName;
        this.routeNumber = routeNumber;
        this.status = status;
        this.dispatchDate = dispatchDate;
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.scheduledArrivalTime = scheduledArrivalTime;
        this.actualDepartureTime = actualDepartureTime;
        this.actualArrivalTime = actualArrivalTime;
    }

    public static DispatchDetailDto from(Dispatch dispatch) {
        return new DispatchDetailDto(
                dispatch.getDispatchId(),
                dispatch.getBus().getBusId(),
                dispatch.getBus().getVehicleNumber(),
                dispatch.getDriver().getUserId(),
                dispatch.getDriver().getUsername(),
                dispatch.getBus().getRouteNumber(),
                dispatch.getStatus(),
                dispatch.getDispatchDate(),
                dispatch.getScheduledDepartureTime(),
                dispatch.getScheduledArrivalTime(),
                dispatch.getActualDepartureTime(),
                dispatch.getActualArrivalTime()
        );
    }
}
