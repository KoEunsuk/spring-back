package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.DispatchStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate; // 배차 날짜 (dispatch_date)
import java.time.LocalTime; // 출발/도착 시간 (scheduled_departure 등)

@Entity
@Table(name = "dispatch")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Dispatch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "dispatch_id")
    private Long dispatchId; // 배차 정보의 고유 식별자

    // ManyToOne 관계: 이 배차에 할당된 Driver (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "driver_id")
    private Driver driver;

    // ManyToOne 관계: 이 배차에 할당된 Bus (N:1 관계)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "bus_id") // DB의 'bus_id' 컬럼이 Bus 테이블을 참조
    private Bus busOld; // 할당된 Bus 엔티티 객체

    @Column(length = 20)
    private DispatchStatus status; // 배차의 현재 상태 (예: "대기", "운행 중", "완료", "취소")

    @Column(name = "dispatch_date")
    private LocalDate dispatchDate; // 배차가 이루어진 날짜

    @Column(name = "scheduled_departure")
    private LocalTime scheduledDeparture; // 예정 출발 시간

    @Column(name = "actual_departure")
    private LocalTime actualDeparture; // 실제 출발 시간

    @Column(name = "actual_arrival")
    private LocalTime actualArrival; // 실제 도착 시간

    @Column(name = "warning_count")
    private Integer warningCount; // 배차 운행 중 발생한 경고 횟수

    @Column(name = "driving_score")
    private Integer drivingScore; //  운전 점수
}