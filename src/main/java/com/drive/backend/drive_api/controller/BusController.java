package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.common.ApiResponse;
import com.drive.backend.drive_api.dto.request.BusCreateDto;
import com.drive.backend.drive_api.dto.request.BusUpdateRequestDto;
import com.drive.backend.drive_api.dto.response.BusDetailDto;
import com.drive.backend.drive_api.service.BusService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/admin/buses")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class BusController {

    private final BusService busService;

    @PostMapping
    public ResponseEntity<ApiResponse<BusDetailDto>> createBus(@Valid @RequestBody BusCreateDto createDto) {
        BusDetailDto responseData = busService.createBus(createDto);
        URI location = URI.create("/api/admin/buses/" + responseData.getBusId());
        ApiResponse<BusDetailDto> response = ApiResponse.success("버스 등록에 성공했습니다.", responseData);
        return ResponseEntity.created(location).body(response);
    }

    @GetMapping
    public ResponseEntity<ApiResponse<List<BusDetailDto>>> getAllBuses() {
        List<BusDetailDto> buses = busService.getAllBuses();
        return ResponseEntity.ok(ApiResponse.success("소속 버스 목록 조회에 성공했습니다.", buses));
    }

    @GetMapping("/{busId}")
    public ResponseEntity<ApiResponse<BusDetailDto>> findBusById(@PathVariable Long busId) {
        BusDetailDto bus = busService.findBusById(busId);
        return ResponseEntity.ok(ApiResponse.success("버스 상세 정보 조회에 성공했습니다.", bus));
    }

    @PatchMapping("/{busId}")
    public ResponseEntity<ApiResponse<BusDetailDto>> updateBus(
            @PathVariable Long busId,
            @Valid @RequestBody BusUpdateRequestDto updateRequest) {
        BusDetailDto updatedBus = busService.updateBus(busId, updateRequest);
        return ResponseEntity.ok(ApiResponse.success("버스 정보 수정에 성공했습니다.", updatedBus));
    }

    @DeleteMapping("/{busId}")
    public ResponseEntity<ApiResponse<Void>> deleteBus(@PathVariable Long busId) {
        busService.deleteBus(busId);
        return ResponseEntity.ok(ApiResponse.success("버스 삭제에 성공했습니다."));
    }}
