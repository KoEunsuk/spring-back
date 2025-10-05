package com.drive.backend.drive_api.dto.websocket;

import lombok.Getter;

@Getter
public class ObdDataResponse {
    private final Long dispatchId;

    // TODO


    public ObdDataResponse(Long dispatchId) {
        this.dispatchId = dispatchId;
        // TODO
    }

    public static ObdDataResponse from(ObdDataRequest request) {
        return new ObdDataResponse(
                request.getDispatchId()
                // TODO
        );
    }
}
