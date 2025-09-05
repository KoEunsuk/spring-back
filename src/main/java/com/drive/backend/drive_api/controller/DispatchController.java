package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.DispatchDto;
import com.drive.backend.drive_api.service.DispatchService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/dispatches")
public class DispatchController {

    private final DispatchService dispatchService;

    public DispatchController(DispatchService dispatchService) {
        this.dispatchService = dispatchService;
    }

    @GetMapping // 모든 배차 조회
    public List<DispatchDto> getAllDispatches() {
        return dispatchService.getAllDispatches();
    }

    @GetMapping("/{id}") // ID로 특정 배차 조회
    public DispatchDto getDispatchById(@PathVariable Long id) {
        return dispatchService.getDispatchById(id);
    }

    @PostMapping // 새 배차 등록
    public ResponseEntity<DispatchDto> addDispatch(@RequestBody DispatchDto dispatchDto) {
        DispatchDto newDispatch = dispatchService.addDispatch(dispatchDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(newDispatch); // 201 Created 응답
    }

    @PutMapping("/{id}") // 배차 정보 업데이트
    public DispatchDto updateDispatch(@PathVariable Long id, @RequestBody DispatchDto dispatchDto) {
        return dispatchService.updateDispatch(id, dispatchDto);
    }

    @DeleteMapping("/{id}") // 배차 삭제
    public ResponseEntity<String> deleteDispatch(@PathVariable Long id) {
        dispatchService.deleteDispatch(id);
        return ResponseEntity.ok("배차 정보 삭제 완료"); // 200 OK 응답
    }
}