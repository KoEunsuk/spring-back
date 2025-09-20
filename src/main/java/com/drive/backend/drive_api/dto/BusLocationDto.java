package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus_old;
import lombok.Getter;

@Getter
public class BusLocationDto {

    private Long busId;



    private BusLocationDto(Bus_old busOld) {

    }

    public static BusLocationDto from(Bus_old busOld) {
        return new BusLocationDto(busOld);
    }
}
