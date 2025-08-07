package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.ScheduleDto;
import com.drive.backend.drive_api.service.ScheduleService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
// @CrossOrigin(origins = "http://localhost:3000") // WebConfig에서 전역 CORS 설정 시 제거 가능
public class ScheduleController {

    private final ScheduleService scheduleService;

    public ScheduleController(ScheduleService scheduleService) {
        this.scheduleService = scheduleService;
    }

    @GetMapping
    public List<ScheduleDto> getAllSchedules() {
        return scheduleService.getAllSchedules();
    }

    @PostMapping
    public ScheduleDto addSchedule(@RequestBody ScheduleDto newSchedule) {
        return scheduleService.addSchedule(newSchedule);
    }

    @PutMapping("/{id}")
    public ScheduleDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleDto updatedSchedule) {
        return scheduleService.updateSchedule(id, updatedSchedule);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteSchedule(@PathVariable Long id) {
        scheduleService.deleteSchedule(id);
        return ResponseEntity.ok("삭제 완료");
    }
}