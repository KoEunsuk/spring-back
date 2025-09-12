package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.enums.DispatchStatus;
import lombok.Getter;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
public class DispatchDetailDto {
    private Long dispatchId;
    private Long driverId;
    private Long busId;
    private DispatchStatus status;
    private LocalDate dispatchDate;
    private LocalTime scheduledDeparture;
    private LocalTime actualDeparture;
    private LocalTime actualArrival;
    private Integer warningCount;
    private Integer drivingScore;

    private DispatchDetailDto(Dispatch dispatch) {
        this.dispatchId = dispatch.getDispatchId();
        this.status = dispatch.getStatus();
        this.dispatchDate = dispatch.getDispatchDate();
        this.scheduledDeparture = dispatch.getScheduledDeparture();
        this.actualDeparture = dispatch.getActualDeparture();
        this.actualArrival = dispatch.getActualArrival();
        this.warningCount = dispatch.getWarningCount();
        this.drivingScore = dispatch.getDrivingScore();

        Driver driver = dispatch.getDriver();
        this.driverId = (driver != null) ? driver.getDriverId() : null;

        Bus bus = dispatch.getBus();
        this.busId = (bus != null) ? bus.getBusId() : null;
    }

    public static DispatchDetailDto from(Dispatch dispatch) {
        return new DispatchDetailDto(dispatch);
    }
}
