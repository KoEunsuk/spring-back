package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.DriverAdminUpdateRequestDto;
import com.drive.backend.drive_api.dto.response.DriverDetailDto;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.security.SecurityUtil;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
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
        Long adminOperatorId = SecurityUtil.getCurrentUser()
                .map(CustomUserDetails::getOperatorId)
                .orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

        return driverRepository.findByOperator_OperatorId(adminOperatorId).stream()
                .map(DriverDetailDto::from)
                .collect(Collectors.toList());
    }

    // 특정 운전자 상세 조회
    public DriverDetailDto findDriverById(Long driverId) {
        Driver driver = findDriverAndCheckPermission(driverId);
        return DriverDetailDto.from(driver);
    }

    // 운전자 정보 수정 (관리자용)
    @Transactional
    public DriverDetailDto updateDriverByAdmin(Long driverId, DriverAdminUpdateRequestDto updateDto) {
        Driver driver = findDriverAndCheckPermission(driverId);

        if (updateDto.getPhoneNumber() != null) driver.setPhoneNumber(updateDto.getPhoneNumber());
        if (updateDto.getLicenseNumber() != null) driver.setLicenseNumber(updateDto.getLicenseNumber());
        if (updateDto.getCareerYears() != null) driver.setCareerYears(updateDto.getCareerYears());
        if (updateDto.getGrade() != null) driver.setGrade(updateDto.getGrade());

        return DriverDetailDto.from(driver);
    }

    // 운전자 물리적 삭제
    @Transactional
    public void deleteDriverByAdmin(Long driverId) {
        Driver driverToDelete = findDriverAndCheckPermission(driverId);

        if (!driverToDelete.getDispatches().isEmpty()) {
            throw new IllegalStateException("해당 운전자에 배차 기록이 존재하여 삭제할 수 없습니다.");
        }

        driverRepository.delete(driverToDelete);
    }

    // 동일 회사 소속인지 검사하기 위한 헬퍼 메서드
    private Driver findDriverAndCheckPermission(Long driverId) {
        CustomUserDetails currentUser = SecurityUtil.getCurrentUser()
                .orElseThrow(() -> new RuntimeException("인증 정보를 찾을 수 없습니다."));
        Long adminOperatorId = currentUser.getOperatorId();

        Driver driver = driverRepository.findById(driverId)
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", driverId));

        Operator driverOperator = driver.getOperator();
        if (driverOperator == null) {
            // 이 경우는 데이터가 잘못된 심각한 상황일 수 있으므로 서버 에러로 처리
            throw new IllegalStateException("Driver " + driverId + " has no assigned operator.");
        }

        if (!adminOperatorId.equals(driverOperator.getOperatorId())) {
            throw new AccessDeniedException("다른 운수사의 직원을 관리할 권한이 없습니다.");
        }

        return driver;
    }
}
