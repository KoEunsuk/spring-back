package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.dto.DriverDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class DriverRepository {

    private static List<DriverDto> drivers = Collections.synchronizedList(new ArrayList<>(List.of(
            new DriverDto(1L, "박진수", "010-1111-2222", "운행 중"),
            new DriverDto(2L, "박윤영", "010-3333-4444", "대기"),
            new DriverDto(3L, "고은석", "010-5555-6666", "운행 중"),
            new DriverDto(4L, "정의태", "010-7777-8888", "이상")
    )));
    private static AtomicLong nextId = new AtomicLong(4);

    public List<DriverDto> findAll() {
        return new ArrayList<>(drivers);
    }

    public Optional<DriverDto> findById(Long id) {
        return drivers.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }

    public DriverDto save(DriverDto driver) {
        if (driver.getId() == null) {
            driver.setId(nextId.incrementAndGet());
            drivers.add(driver);
        } else {
            findById(driver.getId()).ifPresent(existingDriver -> {
                existingDriver.setName(driver.getName());
                existingDriver.setPhone(driver.getPhone());
                existingDriver.setStatus(driver.getStatus());
            });
        }
        return driver;
    }

    public void deleteById(Long id) {
        drivers.removeIf(d -> d.getId().equals(id));
    }
}