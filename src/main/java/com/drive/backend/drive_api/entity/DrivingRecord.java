package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "driving_records")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DrivingRecord {
    @Id
    private Long id;

    @OneToOne(fetch = FetchType.LAZY)
    @MapsId // DrivingRecord의 ID를 Dispatch의 ID와 매핑
    @JoinColumn(name = "dispatch_id")
    private Dispatch dispatch;

    private Integer drowsinessCount = 0;
    private Integer accelerationCount = 0;
    private Integer brakingCount = 0;
    private Integer abnormalCount = 0;
    private Integer drivingScore = 100;

    // 생성자
    public DrivingRecord(Dispatch dispatch) {
        this.dispatch = dispatch;
    }
}
