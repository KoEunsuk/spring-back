package com.drive.backend.drive_api.controller.rest;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.DispatchCreateRequest;
import com.drive.backend.drive_api.dto.response.*;
import com.drive.backend.drive_api.enums.DispatchStatus;
import com.drive.backend.drive_api.service.DispatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static com.drive.backend.drive_api.security.SecurityUtil.getAuthenticatedUser;

@RestController
@RequestMapping("/api/admin/dispatches")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminDispatchController {

    private final DispatchService dispatchService;

    // 신규 배차 생성
    @PostMapping
    public ResponseEntity<ApiResponse<DispatchDetailDto>> createDispatch(@Valid @RequestBody DispatchCreateRequest createRequest) {
        DispatchDetailDto responseData = dispatchService.createDispatch(createRequest, getAuthenticatedUser());
        URI location = URI.create("/api/admin/dispatches/" + responseData.getDispatchId());
        return ResponseEntity.created(location).body(ApiResponse.success("신규 배차 생성에 성공했습니다.", responseData));
    }

    // 배차 목록 조회 by 날짜, 상태
    @GetMapping
    public ResponseEntity<ApiResponse<List<DispatchDetailDto>>> getAllDispatches(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate,
            @RequestParam(required = false) List<DispatchStatus> statuses
    ) {
        List<DispatchDetailDto> dispatches = dispatchService.getDispatchesForAdmin(startDate, endDate, statuses, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 목록 조회에 성공했습니다.", dispatches));
    }

    // 특정 배차 상세 조회
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> getDispatchById(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.getDispatchById(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 정보를 조회했습니다.", responseData));
    }

    // 배차 가능한 버스 목록 조회
    @GetMapping("/available-buses")
    public ResponseEntity<ApiResponse<List<BusDetailDto>>> getAvailableBuses(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<BusDetailDto> availableBuses = dispatchService.findAvailableBuses(startTime, endTime, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 가능한 버스 목록을 조회했습니다.", availableBuses));
    }

    // 배차 가능한 운전자 목록 조회
    @GetMapping("/available-drivers")
    public ResponseEntity<ApiResponse<List<DriverDetailDto>>> getAvailableDrivers(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startTime,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endTime
    ) {
        List<DriverDetailDto> availableDrivers = dispatchService.findAvailableDrivers(startTime, endTime, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 가능한 운전자 목록을 조회했습니다.", availableDrivers));
    }

    // 배차 운행 시작
    @PatchMapping("/{dispatchId}/start")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> startDispatch(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.startDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 운행을 시작합니다.", responseData));
    }

    // 배차 운행 종료
    @PatchMapping("/{dispatchId}/end")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> endDispatch(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.endDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 운행을 종료했습니다.", responseData));
    }

    // 배차 취소(삭제 X)
    @PatchMapping("/{dispatchId}/cancel")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> cancelDispatch(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.cancelDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차를 취소했습니다.", responseData));
    }

    // 특정 배차의 운행 기록(DrivingRecord) 조회
    @GetMapping("/{dispatchId}/driving-record")
    public ResponseEntity<ApiResponse<DrivingRecordResponse>> getDrivingRecord(@PathVariable Long dispatchId) {
        DrivingRecordResponse responseData = dispatchService.getDrivingRecordForDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("운행 기록 조회에 성공했습니다.", responseData));
    }

    // 특정 배차의 운행 이벤트(DrivingEvent) 목록 조회
    @GetMapping("/{dispatchId}/events")
    public ResponseEntity<ApiResponse<List<DrivingEventResponse>>> getDrivingEvents(@PathVariable Long dispatchId) {
        List<DrivingEventResponse> responseData = dispatchService.getDrivingEventsForDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("운행 이벤트 목록 조회에 성공했습니다.", responseData));
    }

    // 특정 배차의 과거 운행 경로 전체 조회
    @GetMapping("/{dispatchId}/locations")
    public ResponseEntity<ApiResponse<List<LocationHistoryResponse>>> getLocationHistories(@PathVariable Long dispatchId) {
        List<LocationHistoryResponse> responseData = dispatchService.getLocationHistoriesForDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("운행 경로 조회에 성공했습니다.", responseData));
    }

}
