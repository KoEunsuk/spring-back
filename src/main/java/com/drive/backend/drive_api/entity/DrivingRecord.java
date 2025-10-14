package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "driving_records")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrivingRecord {

    @Version
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long version;

    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // DrivingRecord의 ID를 Dispatch의 ID와 매핑
    @JoinColumn(name = "dispatch_id")
    private Dispatch dispatch;

    @OneToMany(mappedBy = "drivingRecord", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DrivingEvent> drivingEvents = new ArrayList<>();

    @Column(nullable = false)
    private Integer drowsinessCount = 0;

    @Column(nullable = false)
    private Integer accelerationCount = 0;

    @Column(nullable = false)
    private Integer brakingCount = 0;

    @Column(nullable = false)
    private Integer smokingCount = 0;

    @Column(nullable = false)
    private Integer seatbeltUnfastenedCount = 0;

    @Column(nullable = false)
    private Integer phoneUsageCount = 0;

    @Column(nullable = false)
    private Integer drivingScore = 100;

    // 생성자
    public DrivingRecord(Dispatch dispatch) {
        this.dispatch = dispatch;
    }

    public void addDrivingEvent(DrivingEvent event) {
        this.drivingEvents.add(event);
        if (event.getDrivingRecord() != this) {
            event.setDrivingRecord(this);
        }
    }
}
