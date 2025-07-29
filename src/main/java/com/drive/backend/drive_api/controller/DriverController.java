package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.DriverDetailsDto;
import com.drive.backend.drive_api.dto.DriverStatusDto;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:3000")
public class DriverController {

    @GetMapping
    public List<DriverStatusDto> getAllDrivers() {
        return List.of(
                new DriverStatusDto(1L, "박진수", "운행 중", 37.5665, 126.9780),
                new DriverStatusDto(2L, "박윤영", "대기", 37.5651, 126.9895),
                new DriverStatusDto(3L, "고은석", "이상", 37.5700, 126.9820)
        );
    }

    @GetMapping("/{id}/details")
    public DriverDetailsDto getDriverDetails(@PathVariable Long id) {
        return new DriverDetailsDto(id, "박진수", "소나타", 65, 3000, 70);
    }
}