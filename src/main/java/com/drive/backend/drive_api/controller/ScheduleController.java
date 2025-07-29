package com.drive.backend.drive_api.controller;

import com.drive.backend.drive_api.dto.ScheduleDto;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/schedules")
@CrossOrigin(origins = "http://localhost:3000")
public class ScheduleController {

    private static List<ScheduleDto> schedules = new ArrayList<>(List.of(
            new ScheduleDto(1L, "12가1234", "박진수", LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1), false),
            new ScheduleDto(2L, "34나5678", "박윤영", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(1), true)
    ));

    @GetMapping
    public List<ScheduleDto> getSchedules() {
        return schedules;
    }

    @PostMapping
    public ScheduleDto addSchedule(@RequestBody ScheduleDto newSchedule) {
        newSchedule.setId((long) (schedules.size() + 1));
        schedules.add(newSchedule);
        return newSchedule;
    }

    @PutMapping("/{id}")
    public ScheduleDto updateSchedule(@PathVariable Long id, @RequestBody ScheduleDto updatedSchedule) {
        for (ScheduleDto s : schedules) {
            if (s.getId().equals(id)) {
                s.setVehicleNumber(updatedSchedule.getVehicleNumber());
                s.setDriverName(updatedSchedule.getDriverName());
                s.setStartTime(updatedSchedule.getStartTime());
                s.setEndTime(updatedSchedule.getEndTime());
                s.setCompleted(updatedSchedule.isCompleted());
                return s;
            }
        }
        throw new RuntimeException("스케줄 없음");
    }

    @DeleteMapping("/{id}")
    public String deleteSchedule(@PathVariable Long id) {
        schedules.removeIf(s -> s.getId().equals(id));
        return "삭제 완료";
    }
}