package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "dispatches")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Dispatch {

    @Version
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long version;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long dispatchId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id", nullable = false)
    private Bus bus;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id", nullable = false)
    private Driver driver;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DispatchStatus status;

    @Column(nullable = false)
    private LocalDate dispatchDate;

    // 예정 시간
    @Column(nullable = false)
    private LocalDateTime scheduledDepartureTime;

    @Column(nullable = false)
    private LocalDateTime scheduledArrivalTime;

    // 실제 시간
    private LocalDateTime actualDepartureTime;
    private LocalDateTime actualArrivalTime;

    @OneToOne(mappedBy = "dispatch", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private DrivingRecord drivingRecord;

    @OneToMany(mappedBy = "dispatch")
    private List<Notification> notifications = new ArrayList<>();

    @OneToMany(mappedBy = "dispatch", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<LocationHistory> locationHistories = new ArrayList<>();

    // 필수값 생성자
    public Dispatch(Bus bus, Driver driver, LocalDateTime scheduledDepartureTime, LocalDateTime scheduledArrivalTime) {
        this.bus = bus;
        this.driver = driver;
        this.scheduledDepartureTime = scheduledDepartureTime;
        this.scheduledArrivalTime = scheduledArrivalTime;

        this.dispatchDate = scheduledDepartureTime.toLocalDate();
        this.status = DispatchStatus.SCHEDULED;
    }

    // 연관관계 편의 메서드
    public void setDrivingRecord(DrivingRecord drivingRecord) {
        this.drivingRecord = drivingRecord;
        if (drivingRecord.getDispatch() != this) {
            drivingRecord.setDispatch(this);
        }
    }

    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        if (notification.getDispatch() != this) {
            notification.setDispatch(this);
        }
    }

    public void addLocationHistory(LocationHistory locationHistory) {
        this.locationHistories.add(locationHistory);
        if (locationHistory.getDispatch() != this) {
            locationHistory.setDispatch(this);
        }
    }
}
