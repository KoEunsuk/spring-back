package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.repository.DriverRepo;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/drivers")
public class DriverCon {

    private final DriverRepo driverRepository;

    public DriverCon(DriverRepo driverRepository) {
        this.driverRepository = driverRepository;
    }

    @GetMapping
    public List<Driver> getAllDrivers() {
        return driverRepository.findAll();
    }

    @GetMapping("/{id}")
    public Driver getDriverById(@PathVariable Long id) {
        return driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
    }

    @PostMapping
    public Driver createDriver(@RequestBody Driver driver) {
        return driverRepository.save(driver);
    }

    @PutMapping("/{id}")
    public Driver updateDriver(@PathVariable Long id, @RequestBody Driver driver) {
        Driver existing = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Driver not found"));
        existing.setName(driver.getName());
        existing.setPhone(driver.getPhone());
        existing.setStatus(driver.getStatus());
        return driverRepository.save(existing);
    }

    @DeleteMapping("/{id}")
    public void deleteDriver(@PathVariable Long id) {
        driverRepository.deleteById(id);
    }
}
