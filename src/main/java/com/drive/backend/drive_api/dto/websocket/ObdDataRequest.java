package com.drive.backend.drive_api.dto.websocket;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter
public class ObdDataRequest {

    @NotNull
    private Long dispatchId;

    // TODO

}
