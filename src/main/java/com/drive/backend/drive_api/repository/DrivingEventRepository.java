package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.DrivingEvent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DrivingEventRepository extends JpaRepository<DrivingEvent, Long> {

    List<DrivingEvent> findAllByDrivingRecord_Dispatch_Driver_UserIdAndEventTimestampBetweenOrderByEventTimestampDesc(
            Long userId,
            LocalDateTime start,
            LocalDateTime end
    );
}
