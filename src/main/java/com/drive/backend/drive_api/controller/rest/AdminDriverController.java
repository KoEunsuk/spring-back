package com.drive.backend.drive_api.controller.rest;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.DriverAdminUpdateRequest;
import com.drive.backend.drive_api.dto.response.DispatchDetailResponse;
import com.drive.backend.drive_api.dto.response.DriverDetailResponse;
import com.drive.backend.drive_api.dto.response.DrivingEventResponse;
import com.drive.backend.drive_api.service.DriverService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
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
    
    // 특정 운전자 운행 이벤트 목록 조회 (특정 기간 최신순)
    @GetMapping("/{driverId}/events")
    public ResponseEntity<ApiResponse<List<DrivingEventResponse>>> getDriverEvents(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DrivingEventResponse> events = driverService.getDrivingEventsForDriver(driverId, startDate, endDate);

        return ResponseEntity.ok(ApiResponse.success("운전자의 운행 이벤트 목록 조회가 완료되었습니다.", events));
    }

    // 특정 운전자 배차 목록 조회 (특정 기간 최신순)
    @GetMapping("/{driverId}/dispatches")
    public ResponseEntity<ApiResponse<List<DispatchDetailResponse>>> getDriverDispatches(
            @PathVariable Long driverId,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DispatchDetailResponse> dispatches = driverService.getDispatchesForDriver(driverId, startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("운전자의 배차 목록 조회가 완료되었습니다.", dispatches));
    }
}