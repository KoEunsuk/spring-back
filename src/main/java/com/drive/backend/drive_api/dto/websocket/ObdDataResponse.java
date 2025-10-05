package com.drive.backend.drive_api.dto.websocket;

import lombok.Getter;

@Getter
public class ObdDataResponse {

    private final Long dispatchId;

    private final boolean stalled;
    private final Double speed;
    private final Double soc;
    private final Double engineRpm;
    private final Double torque;
    private final Double brake;
    private final Double throttle;
    private final Double clutch;


    // TODO - 자료형 점검


    public ObdDataResponse(Long dispatchId, boolean stalled, Double speed, Double soc, Double engineRpm, Double torque, Double brake, Double throttle, Double clutch) {
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
