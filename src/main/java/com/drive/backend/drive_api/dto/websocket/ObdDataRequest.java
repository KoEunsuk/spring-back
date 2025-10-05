package com.drive.backend.drive_api.dto.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ObdDataRequest {

    @NotNull
    private Long dispatchId;

    private boolean stalled;
    private Long speed;
    private Long soc;
    private Long engineRpm;
    private Long torque;
    private Long brake;
    private Long throttle;
    private Long clutch;

    // TODO - 자료형 및 valid적용

}
