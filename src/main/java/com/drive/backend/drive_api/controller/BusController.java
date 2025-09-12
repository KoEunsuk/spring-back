package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.service.BusService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController {

    private final BusService busService;

    public BusController(BusService busService) {
        this.busService = busService;
    }

    @GetMapping
    public List<BusListDto> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/{busId}")
    public BusDto getBusById(@PathVariable Long busId) {
        return busService.getBusById(busId);
    }

    @PostMapping
    public ResponseEntity<BusDto> addBus(@RequestBody BusDto busDto) {
        BusDto newBus = busService.addBus(busDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBus);
    }

    @PutMapping("/{busId}")
    public ResponseEntity<BusDto> updateBus(@PathVariable Long busId, @RequestBody BusUpdateRequestDto updateDto) {
        BusDto updatedBus = busService.updateBus(busId, updateDto);

        return ResponseEntity.ok(updatedBus);
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {
        busService.deleteBus(busId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/locations")
    public ResponseEntity<List<BusLocationDto>> getAllBusesLocations() {
        List<BusLocationDto> locations = busService.getAllBusesLocations();
        return ResponseEntity.ok(locations);
    }
}