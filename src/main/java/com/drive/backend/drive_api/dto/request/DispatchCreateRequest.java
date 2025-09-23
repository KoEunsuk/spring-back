package com.drive.backend.drive_api.dto.request;

import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DispatchCreateRequest {
    private Long busId;
    private Long driverId;
    private LocalDateTime scheduledDepartureTime;
    private LocalDateTime scheduledArrivalTime;
}
