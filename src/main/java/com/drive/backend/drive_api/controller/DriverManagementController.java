package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.DriverDto;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/drivers")
@CrossOrigin(origins = "http://localhost:3000")
public class DriverManagementController {

    private static List<DriverDto> drivers = new ArrayList<>(List.of(
            new DriverDto(1L, "박진수", "010-1234-5678", "운행 중"),
            new DriverDto(2L, "박윤영", "010-9876-5432", "대기")
    ));

    @GetMapping
    public List<DriverDto> getDrivers() {
        return drivers;
    }

    @PostMapping
    public DriverDto addDriver(@RequestBody DriverDto newDriver) {
        newDriver.setId((long) (drivers.size() + 1));
        drivers.add(newDriver);
        return newDriver;
    }

    @PutMapping("/{id}")
    public DriverDto updateDriver(@PathVariable Long id, @RequestBody DriverDto updatedDriver) {
        for (DriverDto d : drivers) {
            if (d.getId().equals(id)) {
                d.setName(updatedDriver.getName());
                d.setPhone(updatedDriver.getPhone());
                d.setStatus(updatedDriver.getStatus());
                return d;
            }
        }
        throw new RuntimeException("운전자 없음");
    }

    @DeleteMapping("/{id}")
    public String deleteDriver(@PathVariable Long id) {
        drivers.removeIf(d -> d.getId().equals(id));
        return "삭제 완료";
    }
}