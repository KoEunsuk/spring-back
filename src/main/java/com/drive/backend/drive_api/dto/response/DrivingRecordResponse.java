package com.drive.backend.drive_api.dto.response;

import com.drive.backend.drive_api.entity.DrivingRecord;
import lombok.Getter;

@Getter
public class DrivingRecordResponse {
    private final Long dispatchId;
    private final Integer drowsinessCount;
    private final Integer accelerationCount;
    private final Integer brakingCount;
    private final Integer abnormalCount;
    private final Integer drivingScore;

    private DrivingRecordResponse(Long dispatchId, Integer drowsinessCount, Integer accelerationCount, Integer brakingCount, Integer abnormalCount, Integer drivingScore) {
        this.dispatchId = dispatchId;
        this.drowsinessCount = drowsinessCount;
        this.accelerationCount = accelerationCount;
        this.brakingCount = brakingCount;
        this.abnormalCount = abnormalCount;
        this.drivingScore = drivingScore;
    }

    public static DrivingRecordResponse from(DrivingRecord record) {
        return new DrivingRecordResponse(
                record.getId(),
                record.getDrowsinessCount(),
                record.getAccelerationCount(),
                record.getBrakingCount(),
                record.getAbnormalCount(),
                record.getDrivingScore()
        );
    }
}
