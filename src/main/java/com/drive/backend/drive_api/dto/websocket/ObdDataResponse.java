package com.drive.backend.drive_api.dto.websocket;

import lombok.Getter;

@Getter
public class ObdDataResponse {

    private final Long dispatchId;

    private final boolean stalled;
    private final Long speed;
    private final Long soc;
    private final Long engineRpm;
    private final Long torque;
    private final Long brake;
    private final Long throttle;
    private final Long clutch;


    // TODO - 자료형 점검


    public ObdDataResponse(Long dispatchId, boolean stalled, Long speed, Long soc, Long engineRpm, Long torque, Long brake, Long throttle, Long clutch) {
        this.dispatchId = dispatchId;

        this.stalled = stalled;
        this.speed = speed;
        this.soc = soc;
        this.engineRpm = engineRpm;
        this.torque = torque;
        this.brake = brake;
        this.throttle = throttle;
        this.clutch = clutch;
        // TODO
    }

    public static ObdDataResponse from(ObdDataRequest request) {
        return new ObdDataResponse(
                request.getDispatchId(),

                request.isStalled(),
                request.getSpeed(),
                request.getSoc(),
                request.getEngineRpm(),
                request.getTorque(),
                request.getBrake(),
                request.getThrottle(),
                request.getClutch()
                // TODO
        );
    }
}
