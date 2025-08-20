package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.DriverDetailsDto;
import com.drive.backend.drive_api.dto.DriverDto;
import com.drive.backend.drive_api.dto.DriverStatusDto;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.repository.DriverRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository; // DriverRepository 주입

    // 생성자 주입
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    // 운전자 관리: 모든 운전자 목록 조회
    public List<DriverDto> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(d -> new DriverDto(d.getDriverId(), d.getDriverName()))
                .toList();
    }

    // 운전자 관리: 새로운 운전자 등록
    public DriverDto addDriver(DriverDto driverDto) {
        Driver driver = new Driver();
        driver.setDriverName(driverDto.getName());

        //테스트용 비번 삽입
        driver.setDriverPassword("1234");

        Driver saved = driverRepository.save(driver);
        return new DriverDto(saved.getDriverId(), saved.getDriverName());
    }

    // 운전자 관리: ID로 운전자 상세 조회
    public DriverDto getDriverById(Long id) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("운전자를 찾을 수 없습니다: " + id));

        return new DriverDto(driver.getDriverId(), driver.getDriverName());
    }

    // 운전자 관리: 운전자 정보 업데이트
    public DriverDto updateDriver(Long id, DriverDto updatedDriver) {
        Driver driver = driverRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("운전자를 찾을 수 없습니다: " + id));

        driver.setDriverName(updatedDriver.getName());

        Driver saved = driverRepository.save(driver);
        return new DriverDto(saved.getDriverId(), saved.getDriverName()); // 업데이트 수행
    }

    // 운전자 관리: 운전자 삭제
    public void deleteDriver(Long id) {
        driverRepository.deleteById(id);
    }

//    // 실시간 모니터링: 현재 운행 중인 운전자들 상태 조회 (메인 지도? 표기)
//    public List<DriverStatusDto> getCurrentDriverStatuses() {
//        return driverRepository.findAll().stream()
//                .filter(d -> "운행 중".equals(d.getStatus()) || "이상".equals(d.getStatus()))
//                .map(d -> new DriverStatusDto(
//                        d.getId(),
//                        d.getName(),
//                        d.getStatus(),
//                        37.5 + (d.getId() * 0.001), // 임시 위도
//                        127.0 + (d.getId() * 0.001)  // 임시 경도
//                ))
//                .collect(Collectors.toList());
//    }

//    // 실시간 모니터링: 운전자의 상세 차량 정보 (OBD2 데이터)
//    public DriverDetailsDto getDriverDetails(Long id) {
//        return driverRepository.findById(id)
//                .map(d -> new DriverDetailsDto(
//                        d.getId(),
//                        d.getName(),
//                        "차량모델 " + d.getId(), // 임시 차량 모델
//                        (int)(Math.random() * 100), // 임시 속도
//                        (int)(Math.random() * 5000), // 임시 RPM
//                        (int)(Math.random() * 100) // 임시 연료량
//                ))
//                .orElseThrow(() -> new RuntimeException("운전자를 찾을 수 없습니다: " + id));
//    }
}