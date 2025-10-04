package com.drive.backend.drive_api.dto.websocket;

import lombok.Getter;

@Getter
public class LocationUpdateResponse {
    private final Long dispatchId;
    private final Double latitude;
    private final Double longitude;
    private final String driverName;
    private final String vehicleNumber;

    public LocationUpdateResponse(Long dispatchId, Double latitude, Double longitude, String driverName, String vehicleNumber) {
        this.dispatchId = dispatchId;
        this.latitude = latitude;
        this.longitude = longitude;
        this.driverName = driverName;
        this.vehicleNumber = vehicleNumber;
    }
}
