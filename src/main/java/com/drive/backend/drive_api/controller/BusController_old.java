package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.service.BusService_old;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/buses")
public class BusController_old {

    private final BusService_old busServiceOld;

    public BusController_old(BusService_old busServiceOld) {
        this.busServiceOld = busServiceOld;
    }

    @GetMapping
    public List<BusListDto> getAllBuses() {
        return busServiceOld.getAllBuses();
    }

    @GetMapping("/{busId}")
    public BusDto getBusById(@PathVariable Long busId) {
        return busServiceOld.getBusById(busId);
    }

    @PostMapping
    public ResponseEntity<BusDto> addBus(@RequestBody BusDto busDto) {
        BusDto newBus = busServiceOld.addBus(busDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBus);
    }

    @PutMapping("/{busId}")
    public ResponseEntity<BusDto> updateBus(@PathVariable Long busId, @RequestBody BusUpdateRequestDto updateDto) {
        BusDto updatedBus = busServiceOld.updateBus(busId, updateDto);

        return ResponseEntity.ok(updatedBus);
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<Void> deleteBus(@PathVariable Long busId) {
        busServiceOld.deleteBus(busId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/locations")
    public ResponseEntity<List<BusLocationDto>> getAllBusesLocations() {
        List<BusLocationDto> locations = busServiceOld.getAllBusesLocations();
        return ResponseEntity.ok(locations);
    }
}