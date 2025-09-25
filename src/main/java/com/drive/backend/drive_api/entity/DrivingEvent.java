package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.DrivingEventType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "driving_events")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrivingEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long drivingEventId;

    // 어떤 운행 기록에 속한 이벤트인지 참조
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driving_record_id", nullable = false)
    private DrivingRecord drivingRecord;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DrivingEventType eventType;

    @Column(nullable = false)
    private LocalDateTime eventTimestamp;

    public DrivingEvent(DrivingRecord drivingRecord, DrivingEventType eventType, LocalDateTime eventTimestamp) {
        this.drivingRecord = drivingRecord;
        this.eventType = eventType;
        this.eventTimestamp = eventTimestamp;
    }
}