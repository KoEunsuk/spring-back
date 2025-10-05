package com.drive.backend.drive_api.dto.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ObdDataRequest {

    @NotNull
    private Long dispatchId;

    private boolean stalled;
    private Double speed;
    private Double soc;
    private Double engineRpm;
    private Double torque;
    private Double brake;
    private Double throttle;
    private Double clutch;

    // TODO - 자료형 및 valid적용

}
