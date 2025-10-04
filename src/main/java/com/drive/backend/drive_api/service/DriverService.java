package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.DriverAdminUpdateRequest;
import com.drive.backend.drive_api.dto.response.DriverDetailResponse;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.exception.BusinessException;
import com.drive.backend.drive_api.exception.ErrorCode;
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
    public List<DriverDetailResponse> findAllDrivers() {
        Long adminOperatorId = SecurityUtil.getCurrentUser()
                .map(CustomUserDetails::getOperatorId)
                .orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

        return driverRepository.findAllByOperator_OperatorId(adminOperatorId).stream()
                .map(DriverDetailResponse::from)
                .collect(Collectors.toList());
    }

    // 특정 운전자 상세 조회
    public DriverDetailResponse findDriverById(Long driverId) {
        Driver driver = findDriverAndCheckPermission(driverId);
        return DriverDetailResponse.from(driver);
    }

    // 운전자 정보 수정 (관리자용)
    @Transactional
    public DriverDetailResponse updateDriverByAdmin(Long driverId, DriverAdminUpdateRequest updateDto) {
        Driver driver = findDriverAndCheckPermission(driverId);

        if (updateDto.getPhoneNumber() != null) driver.setPhoneNumber(updateDto.getPhoneNumber());
        if (updateDto.getLicenseNumber() != null) driver.setLicenseNumber(updateDto.getLicenseNumber());
        if (updateDto.getCareerYears() != null) driver.setCareerYears(updateDto.getCareerYears());
        if (updateDto.getGrade() != null) driver.setGrade(updateDto.getGrade());

        return DriverDetailResponse.from(driver);
    }

    // 운전자 물리적 삭제
    @Transactional
    public void deleteDriverByAdmin(Long driverId) {
        Driver driverToDelete = findDriverAndCheckPermission(driverId);

        if (!driverToDelete.getDispatches().isEmpty()) {
            throw new BusinessException(ErrorCode.DRIVER_HAS_DISPATCHES);
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
            throw new BusinessException(ErrorCode.DRIVER_WITHOUT_OPERATOR);
        }

        if (!adminOperatorId.equals(driverOperator.getOperatorId())) {
            throw new AccessDeniedException("다른 운수사의 직원을 관리할 권한이 없습니다.");
        }

        return driver;
    }
}
