package com.drive.backend.drive_api.service;


import com.drive.backend.drive_api.dto.DriverDto;

import com.drive.backend.drive_api.dto.DriverGetDto;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository; // DriverRepository 주입

    // 생성자 주입
    public DriverService(DriverRepository driverRepository) {
        this.driverRepository = driverRepository;
    }

    //DriverDto -? 엔티티 변환 헬퍼 -> 간소화 해서 Dto에는 이름, id, 비번만 존재
    private Driver toEntity(DriverDto dto) {
        Driver driver = new Driver();
        if (dto.getDriverId() != null) {
            driver.setDriverId(dto.getDriverId()); // ID가 있으면 세팅
        }
        driver.setDriverName(dto.getDriverName());       // 이름 세팅
        driver.setDriverPassword(dto.getDriverPassword()); // 비밀번호 세팅

        return driver;
    }

    private DriverDto toDto(Driver entity) {
        return new DriverDto(
                entity.getDriverId(),
                entity.getDriverName(),
                entity.getDriverPassword()
        );


    }

    //모든 운전자 조회.
    @Transactional(readOnly = true)
    public List<DriverGetDto> getAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverGetDto::new)
                .collect(Collectors.toList());
    }

    // 운전자 관리: ID로 상세조회.
    @Transactional(readOnly = true)
    public DriverGetDto getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        return new DriverGetDto(driver);
    }

    @Transactional
    public DriverDto addDriver(DriverDto driverDto) {

        if (driverRepository.findByDriverName(driverDto.getDriverName()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 운전자 이름입니다.");
        }

        Driver driver = toEntity(driverDto); // DTO -> 엔티티 변환

        //  현재 이 코드는 DTO에서 받은 비밀번호(평문일 가능성 높음)를 그대로 DB에 저장함
        //  나중에 AuthService를 통해 비밀번호 해싱을 거쳐야함.
        Driver savedDriver = driverRepository.save(driver); // DB에 저장
        return toDto(savedDriver); // 저장된 엔티티 -> DTO 변환
    }


    //운전자 관리: 운전자 정보 업데이트.
    @Transactional
    public DriverDto updateDriver(Long id, DriverDto updatedDriverDto) { // ID는 Long
        Driver existingDriver = driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id)); //

        //DTO에서 받은 정보로 이름과 비밀번호만 업데이트
        existingDriver.setDriverName(updatedDriverDto.getDriverName());
        existingDriver.setDriverPassword(updatedDriverDto.getDriverPassword());

        Driver savedDriver = driverRepository.save(existingDriver);
        return toDto(savedDriver);
    }

    //  운전자 관리: 운전자 삭제
    @Transactional
    public void deleteDriver(Long id) {
        // 삭제 전 존재 여부를 확인해야함.
        driverRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", id)); // ResourceNotFoundException 사용
        driverRepository.deleteById(id);
    }
}


/** 비밀번호 정상작동되면 위 코드로 하는걸 추천함. 안되면 말좀요~
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
 //                        37.5 + (d.getId() *  0.001), // 임시 위도
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
 }
 /**
 *
 */



