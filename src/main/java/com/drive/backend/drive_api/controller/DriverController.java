package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.DriverDetailsDto;
import com.drive.backend.drive_api.dto.DriverDto;
import com.drive.backend.drive_api.dto.DriverGetDto;
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

    @GetMapping
    public List<DriverGetDto> getAllDriversForManagement() {
        return driverService.getAllDrivers();
    }

    @PostMapping("/management")
    public DriverDto addDriver(@RequestBody DriverDto newDriver) {
        return driverService.addDriver(newDriver);
    }

    @GetMapping("/{driverId}")
    public DriverGetDto getDriverByIdForManagement(@PathVariable Long driverId) {
        return driverService.getDriverById(driverId);
    }

    @PutMapping("/{driverId}")
    public DriverGetDto updateDriver(@PathVariable Long driverId, @RequestBody DriverGetDto updatedDriverDto) {
        return driverService.updateDriver(driverId, updatedDriverDto);
    }

    @DeleteMapping("/{driverId}")
    public ResponseEntity<Void> deleteDriver(@PathVariable Long driverId) { // 반환 타입을 Void로 변경
        driverService.deleteDriver(driverId);
        return ResponseEntity.noContent().build(); // 204 No Content 응답 생성
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