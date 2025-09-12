package com.drive.backend.drive_api.service;


import com.drive.backend.drive_api.dto.DriverDto;

import com.drive.backend.drive_api.dto.DriverGetDto;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DriverService {

    private final DriverRepository driverRepository; // DriverRepository 주입
    private final OperatorRepository operatorRepository;

    // 생성자 주입
    public DriverService(DriverRepository driverRepository, OperatorRepository operatorRepository) {
        this.driverRepository = driverRepository;
        this.operatorRepository = operatorRepository;
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
                .map(DriverGetDto::from)
                .collect(Collectors.toList());
    }

    // 운전자 관리: ID로 상세조회.
    @Transactional(readOnly = true)
    public DriverGetDto getDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId));
        return DriverGetDto.from(driver);
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
    public DriverGetDto updateDriver(Long driverId, DriverGetDto updatedDriverDto) { // ID는 Long
        Driver existingDriver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId)); //

        // DTO의 각 필드가 null이 아닐 경우에만 기존 엔티티의 값을 변경
        if (updatedDriverDto.getDriverName() != null) {
            existingDriver.setDriverName(updatedDriverDto.getDriverName());
        }
        if (updatedDriverDto.getPhoneNumber() != null) {
            existingDriver.setPhoneNumber(updatedDriverDto.getPhoneNumber());
        }
        if (updatedDriverDto.getLicenseNumber() != null) {
            existingDriver.setLicenseNumber(updatedDriverDto.getLicenseNumber());
        }
        if (updatedDriverDto.getOperatorId() != null) {
            // DTO에서 받은 operatorId로 Operator 엔티티를 조회합니다.
            Operator operator = operatorRepository.findById(updatedDriverDto.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operator", "operatorId", updatedDriverDto.getOperatorId()));
            // 조회한 Operator 객체 자체를 설정합니다.
            existingDriver.setOperator(operator);
        }
        if (updatedDriverDto.getCareerYears() != null) {
            existingDriver.setCareerYears(updatedDriverDto.getCareerYears());
        }
        if (updatedDriverDto.getGrade() != null) {
            existingDriver.setGrade(updatedDriverDto.getGrade());
        }
        if (updatedDriverDto.getStatus() != null) {
            existingDriver.setStatus(updatedDriverDto.getStatus());
        }
        if (updatedDriverDto.getDriverImagePath() != null) {
            existingDriver.setDriverImagePath(updatedDriverDto.getDriverImagePath());
        }

        // 메서드가 종료될 때 더티 체킹으로 변경된 필드만 자동으로 UPDATE 쿼리가 실행됨
        return DriverGetDto.from(existingDriver);
    }

    //  운전자 관리: 운전자 삭제
    @Transactional
    public void deleteDriver(Long driverId) {
        // 삭제 전 존재 여부를 확인해야함.
        driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "driverId", driverId)); // ResourceNotFoundException 사용
        driverRepository.deleteById(driverId);
    }
}



