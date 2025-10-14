package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.DrivingRecord;
import lombok.Getter;

@Getter
public class DrivingRecordResponse {
    private final Long dispatchId;
    private final Integer drowsinessCount;
    private final Integer accelerationCount;
    private final Integer brakingCount;
    private final Integer smokingCount;
    private final Integer seatbeltUnfastenedCount;
    private final Integer phoneUsageCount;
    private final Integer drivingScore;

    private DrivingRecordResponse(Long dispatchId, Integer drowsinessCount, Integer accelerationCount, Integer brakingCount, Integer smokingCount, Integer seatbeltUnfastenedCount, Integer phoneUsageCount, Integer drivingScore) {
        this.dispatchId = dispatchId;
        this.drowsinessCount = drowsinessCount;
        this.accelerationCount = accelerationCount;
        this.brakingCount = brakingCount;
        this.smokingCount = smokingCount;
        this.seatbeltUnfastenedCount = seatbeltUnfastenedCount;
        this.phoneUsageCount = phoneUsageCount;
        this.drivingScore = drivingScore;
    }

    public static DrivingRecordResponse from(DrivingRecord record) {
        return new DrivingRecordResponse(
                record.getId(),
                record.getDrowsinessCount(),
                record.getAccelerationCount(),
                record.getBrakingCount(),
                record.getSmokingCount(),
                record.getSeatbeltUnfastenedCount(),
                record.getPhoneUsageCount(),
                record.getDrivingScore()
        );
    }
}
