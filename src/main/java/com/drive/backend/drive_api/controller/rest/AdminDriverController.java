package com.drive.backend.drive_api.controller.rest;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.DriverAdminUpdateRequest;
import com.drive.backend.drive_api.dto.response.DriverDetailResponse;
import com.drive.backend.drive_api.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/drivers")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDriverController {

    private final DriverService driverService;

    // 전체 운전자 목록 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<DriverDetailResponse>>> getAllDrivers() {
        List<DriverDetailResponse> drivers = driverService.findAllDrivers();
        return ResponseEntity.ok(ApiResponse.success("전체 운전자 목록 조회 성공", drivers));
    }

    // 운전자 정보 수정
    @PatchMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverDetailResponse>> updateDriver(
            @PathVariable Long driverId,
            @Valid @RequestBody DriverAdminUpdateRequest updateDto) {
        DriverDetailResponse updatedDriver = driverService.updateDriverByAdmin(driverId, updateDto);
        return ResponseEntity.ok(ApiResponse.success("운전자 정보 수정 성공", updatedDriver));
    }

    // 운전자 물리적 삭제
    @DeleteMapping("/{driverId}")
    public ResponseEntity<ApiResponse<Void>> deleteDriver(@PathVariable Long driverId) {
        driverService.deleteDriverByAdmin(driverId);
        return ResponseEntity.ok(ApiResponse.success("운전자 삭제 성공"));
    }
    
    // 특정 운전자 상세 조회
    @GetMapping("/{driverId}")
    public ResponseEntity<ApiResponse<DriverDetailResponse>> getDriverById(@PathVariable Long driverId) {
        DriverDetailResponse driver = driverService.findDriverById(driverId);
        return ResponseEntity.ok(ApiResponse.success("운전자 상세 정보 조회 성공", driver));
    }
}