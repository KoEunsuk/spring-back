package com.drive.backend.drive_api.dto.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class LocationUpdateRequest {
    @NotNull
    private Long dispatchId;
    @NotNull
    private Double latitude;
    @NotNull
    private Double longitude;
}
