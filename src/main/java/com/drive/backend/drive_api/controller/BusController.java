package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.BusDto;
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
    public List<BusDto> getAllBuses() {
        return busService.getAllBuses();
    }

    @GetMapping("/{id}")
    public BusDto getBusById(@PathVariable Long id) {
        return busService.getBusById(id);
    }

    @PostMapping
    public ResponseEntity<BusDto> addBus(@RequestBody BusDto busDto) {
        BusDto newBus = busService.addBus(busDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newBus);
    }

    @PutMapping("/{id}")
    public BusDto updateBus(@PathVariable Long id, @RequestBody BusDto busDto) {
        return busService.updateBus(id, busDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBus(@PathVariable Long id) {
        busService.deleteBus(id);
        return ResponseEntity.ok("버스 삭제 완료");
    }
}