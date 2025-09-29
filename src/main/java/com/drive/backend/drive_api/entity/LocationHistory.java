package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Entity
@Table(name = "location_histories")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class LocationHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long historyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "dispatch_id", nullable = false)
    private Dispatch dispatch;

    @Column(nullable = false)
    private Double latitude;    // 위도

    @Column(nullable = false)
    private Double longitude;   // 경도

    @CreatedDate
    @Column(nullable = false, updatable = false)
    private LocalDateTime recordedAt;

    public LocationHistory(Dispatch dispatch, Double latitude, Double longitude) {
        this.dispatch = dispatch;
        this.latitude = latitude;
        this.longitude = longitude;
    }
}
