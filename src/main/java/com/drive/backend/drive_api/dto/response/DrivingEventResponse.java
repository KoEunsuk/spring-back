package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.DrivingEvent;
import com.drive.backend.drive_api.enums.DrivingEventType;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DrivingEventResponse {
    private final Long drivingEventId;
    private final DrivingEventType eventType;
    private final LocalDateTime eventTimestamp;
    private final Double latitude;
    private final Double longitude;

    private DrivingEventResponse(Long drivingEventId, DrivingEventType eventType, LocalDateTime eventTimestamp, Double latitude, Double longitude) {
        this.drivingEventId = drivingEventId;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public static DrivingEventResponse from(DrivingEvent event) {
        return new DrivingEventResponse(
                event.getDrivingEventId(),
                event.getEventType(),
                event.getEventTimestamp(),
                event.getLatitude(),
                event.getLongitude()
        );
    }
}
