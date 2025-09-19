package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.DriverAdminUpdateRequestDto;
import com.drive.backend.drive_api.dto.DriverDetailDto;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.DriverRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class DriverService {

    private final DriverRepository driverRepository;

    // 전체 운전자 목록 조회
    public List<DriverDetailDto> findAllDrivers() {
        return driverRepository.findAll().stream()
                .map(DriverDetailDto::from)
                .collect(Collectors.toList());
    }

    // 특정 운전자 상세 조회
    public DriverDetailDto findDriverById(Long driverId) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));
        return DriverDetailDto.from(driver);
    }

    // 운전자 정보 수정 (관리자용)
    @Transactional
    public DriverDetailDto updateDriverByAdmin(Long driverId, DriverAdminUpdateRequestDto updateDto) {
        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        if (updateDto.getPhoneNumber() != null) driver.setPhoneNumber(updateDto.getPhoneNumber());
        if (updateDto.getLicenseNumber() != null) driver.setLicenseNumber(updateDto.getLicenseNumber());
        if (updateDto.getCareerYears() != null) driver.setCareerYears(updateDto.getCareerYears());
        if (updateDto.getGrade() != null) driver.setGrade(updateDto.getGrade());

        return DriverDetailDto.from(driver);
    }

    // 운전자 삭제
    @Transactional
    public void deleteDriverByAdmin(Long driverId) {
        Driver driverToDelete = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        driverRepository.delete(driverToDelete);
    }
}
