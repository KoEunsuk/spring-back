package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class DriverDto {
    private Long driverId;
    private String driverName;
    private String driverPassword;
}