package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.dto.DriverDto;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class DriverRepository {

    private static List<DriverDto> drivers = new ArrayList<>(List.of(
            new DriverDto(1L, "박진수", "010-1234-5678", "운행 중"),
            new DriverDto(2L, "박윤영", "010-9876-5432", "대기")
    ));
    private static long nextId = 4L; // 다음 ID를 위한 변수

    // 모든 운전자 조회
    public List<DriverDto> findAll() {
        return new ArrayList<>(drivers); // 원본 리스트 보호를 위해 새 리스트 반환
    }

    // ID로 운전자 조회
    public Optional<DriverDto> findById(Long id) {
        return drivers.stream()
                .filter(d -> d.getId().equals(id))
                .findFirst();
    }

    // 운전자 저장 또는 업데이트
    public DriverDto save(DriverDto driver) {
        if (driver.getId() == null) { // 새로운 운전자
            driver.setId(nextId++);
            drivers.add(driver);
        } else { // 기존 운전자 업데이트
            findById(driver.getId()).ifPresent(existingDriver -> {
                existingDriver.setName(driver.getName());
                existingDriver.setPhone(driver.getPhone());
                existingDriver.setStatus(driver.getStatus());
            });
        }
        return driver;
    }

    // ID로 운전자 삭제
    public void deleteById(Long id) {
        drivers.removeIf(d -> d.getId().equals(id));
    }
}