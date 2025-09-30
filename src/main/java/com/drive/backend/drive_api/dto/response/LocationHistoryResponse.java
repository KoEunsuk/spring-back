package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.LocationHistory;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class LocationHistoryResponse {
    private final Double latitude;
    private final Double longitude;
    private final LocalDateTime recordedAt;

    private LocationHistoryResponse(Double latitude, Double longitude, LocalDateTime recordedAt) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.recordedAt = recordedAt;
    }

    public static LocationHistoryResponse from(LocationHistory history) {
        return new LocationHistoryResponse(
                history.getLatitude(),
                history.getLongitude(),
                history.getRecordedAt()
        );
    }
}