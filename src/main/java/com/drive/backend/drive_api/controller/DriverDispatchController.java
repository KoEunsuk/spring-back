package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.response.DispatchDetailDto;
import com.drive.backend.drive_api.dto.response.DrivingEventResponse;
import com.drive.backend.drive_api.dto.response.DrivingRecordResponse;
import com.drive.backend.drive_api.service.DispatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

import static com.drive.backend.drive_api.security.SecurityUtil.getAuthenticatedUser;

@RestController
@RequestMapping("/api/driver/me/dispatches")
@PreAuthorize("hasRole('DRIVER')")
@RequiredArgsConstructor
public class DriverDispatchController {

    private final DispatchService dispatchService;

    // 지정된 날짜 범위 사이의 자신의 배차 조회
    @GetMapping
    public ResponseEntity<ApiResponse<List<DispatchDetailDto>>> getMyDispatchesByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate
    ) {
        List<DispatchDetailDto> responseData = dispatchService.getDispatchesForDriverByDateRange(getAuthenticatedUser(), startDate, endDate);
        return ResponseEntity.ok(ApiResponse.success("지정된 기간의 배차 목록을 조회했습니다.", responseData));
    }

    // 자신에게 할당된 특정 배차 상세 조회
    @GetMapping("/{dispatchId}")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> getMyDispatchById(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.getDispatchById(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 상세 정보를 조회했습니다.", responseData));
    }

    // 자신의 배차 운행 시작
    @PatchMapping("/{dispatchId}/start")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> startMyDispatch(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.startDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 운행을 시작합니다.", responseData));
    }

    // 자신의 배차 운행 종료
    @PatchMapping("/{dispatchId}/end")
    public ResponseEntity<ApiResponse<DispatchDetailDto>> endMyDispatch(@PathVariable Long dispatchId) {
        DispatchDetailDto responseData = dispatchService.endDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("배차 운행을 종료했습니다.", responseData));
    }

    // 자신 특정 배차 운행 기록(DrivingRecord) 조회
    @GetMapping("/{dispatchId}/driving-record")
    public ResponseEntity<ApiResponse<DrivingRecordResponse>> getMyDrivingRecord(@PathVariable Long dispatchId) {
        DrivingRecordResponse responseData = dispatchService.getDrivingRecordForDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("운행 기록 조회에 성공했습니다.", responseData));
    }

    // 자신 특정 배차의 운행 이벤트(DrivingEvent) 목록 조회
    @GetMapping("/{dispatchId}/events")
    public ResponseEntity<ApiResponse<List<DrivingEventResponse>>> getMyDrivingEvents(@PathVariable Long dispatchId) {
        List<DrivingEventResponse> responseData = dispatchService.getDrivingEventsForDispatch(dispatchId, getAuthenticatedUser());
        return ResponseEntity.ok(ApiResponse.success("운행 이벤트 목록 조회에 성공했습니다.", responseData));
    }
}
