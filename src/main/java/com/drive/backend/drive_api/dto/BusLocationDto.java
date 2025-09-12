package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Bus;
import lombok.Getter;

@Getter
public class BusLocationDto {

    private Long busId;



    private BusLocationDto(Bus bus) {

    }

    public static BusLocationDto from(Bus bus) {
        return new BusLocationDto(bus);
    }
}
