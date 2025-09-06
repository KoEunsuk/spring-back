package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "alert")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Alert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "alert_id")
    private Long alertId;

    // ManyToOne 관계: 어떤 운전자에게서 감지된 이벤트인지 확인.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = true)
    private Driver driver; // 해당 알림을 발생시킨 Driver 정보

    // ManyToOne 관계: 어떤 버스에서 감지된 이벤트인지를 확인해야해서.
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = true) // bus_id를 외래키로 사용 (Null 허용)
    private Bus bus; // 해당 알림이 발생한 Bus 정보

    @Column(name = "alert_type", length = 50, nullable = false)
    private String alertType; // 알림의 종류

    @Column(length = 20)
    private String severity; // 알림의 심각도?

    @Column(nullable = false)
    private String message; // 알림 내용

    @Column(name = "timestamp", nullable = false)
    private LocalDateTime timestamp; // 알림이 발생한 시각

}