package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.dto.ScheduleDto;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

@Repository
public class ScheduleRepository {

    private static List<ScheduleDto> schedules = Collections.synchronizedList(new ArrayList<>(List.of(
            new ScheduleDto(1L, "12가1234", "박진수", LocalDateTime.now().minusHours(2), LocalDateTime.now().plusHours(1), false),
            new ScheduleDto(2L, "34나5678", "박윤영", LocalDateTime.now().minusHours(5), LocalDateTime.now().minusHours(1), true),
            new ScheduleDto(3L, "56다9012", "고은석", LocalDateTime.now().minusMinutes(30), LocalDateTime.now().plusHours(2), false)
    )));
    private static AtomicLong nextId = new AtomicLong(3);

    public List<ScheduleDto> findAll() {
        return new ArrayList<>(schedules);
    }

    public Optional<ScheduleDto> findById(Long id) {
        return schedules.stream()
                .filter(s -> s.getId().equals(id))
                .findFirst();
    }

    public ScheduleDto save(ScheduleDto schedule) {
        if (schedule.getId() == null) {
            schedule.setId(nextId.incrementAndGet());
            schedules.add(schedule);
        } else {
            findById(schedule.getId()).ifPresent(existingSchedule -> {
                existingSchedule.setVehicleNumber(schedule.getVehicleNumber());
                existingSchedule.setDriverName(schedule.getDriverName());
                existingSchedule.setStartTime(schedule.getStartTime());
                existingSchedule.setEndTime(schedule.getEndTime());
                existingSchedule.setCompleted(schedule.isCompleted());
            });
        }
        return schedule;
    }

    public void deleteById(Long id) {
        schedules.removeIf(s -> s.getId().equals(id));
    }
}