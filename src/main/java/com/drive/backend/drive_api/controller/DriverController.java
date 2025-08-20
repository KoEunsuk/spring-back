package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.DriverDetailsDto;
import com.drive.backend.drive_api.dto.DriverDto;
import com.drive.backend.drive_api.dto.DriverStatusDto;
import com.drive.backend.drive_api.service.DriverService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
// @CrossOrigin(origins = "http://localhost:3000") // WebConfig에서 전역 CORS 설정 시 제거 가능
public class DriverController {

    private final DriverService driverService;

    public DriverController(DriverService driverService) {
        this.driverService = driverService;
    }

    @GetMapping("/management")
    public List<DriverDto> getAllDriversForManagement() {
        return driverService.getAllDrivers();
    }

    @PostMapping("/management")
    public DriverDto addDriver(@RequestBody DriverDto newDriver) {
        return driverService.addDriver(newDriver);
    }

    @GetMapping("/management/{id}")
    public DriverDto getDriverByIdForManagement(@PathVariable Long id) {
        return driverService.getDriverById(id);
    }

    @PutMapping("/management/{id}")
    public DriverDto updateDriver(@PathVariable Long id, @RequestBody DriverDto updatedDriver) {
        return driverService.updateDriver(id, updatedDriver);
    }

    @DeleteMapping("/management/{id}")
    public ResponseEntity<String> deleteDriver(@PathVariable Long id) {
        driverService.deleteDriver(id);
        return ResponseEntity.ok("삭제 완료");
    }

//    @GetMapping("/status")
//    public List<DriverStatusDto> getAllDriverStatuses() {
//        return driverService.getCurrentDriverStatuses();
//    }
//
//    @GetMapping("/{id}/details")
//    public DriverDetailsDto getDriverDetails(@PathVariable Long id) {
//        return driverService.getDriverDetails(id);
//    }
}