package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.ScheduleDto;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.ScheduleRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ScheduleService {

    private final ScheduleRepository scheduleRepository;

    public ScheduleService(ScheduleRepository scheduleRepository) {
        this.scheduleRepository = scheduleRepository;
    }

    public List<ScheduleDto> getAllSchedules() {
        return scheduleRepository.findAll();
    }

    public ScheduleDto addSchedule(ScheduleDto schedule) {
        return scheduleRepository.save(schedule);
    }

    public ScheduleDto updateSchedule(Long id, ScheduleDto updatedSchedule) {
        scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        updatedSchedule.setId(id);
        return scheduleRepository.save(updatedSchedule);
    }

    public void deleteSchedule(Long id) {
        scheduleRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Schedule", "id", id));
        scheduleRepository.deleteById(id);
    }
}