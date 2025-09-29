package com.drive.backend.drive_api.dto.websocket;

import lombok.Getter;

@Getter
public class LocationUpdateRequest {
    private Long dispatchId;
    private Double latitude;
    private Double longitude;
}
