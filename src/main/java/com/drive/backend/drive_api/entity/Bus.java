package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.FuelType;
import com.drive.backend.drive_api.enums.RouteType;
import com.drive.backend.drive_api.enums.VehicleType;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buses")
@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long busId;

    @Column
    private String routeNumber;

    @Enumerated(EnumType.STRING)
    @Column
    private RouteType routeType;

    @Column(nullable = false)
    private Integer capacity;

    @Column(nullable = false, unique = true)
    private String vehicleNumber;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private VehicleType vehicleType;

    @Column(nullable = false)
    private Integer vehicleYear;

    @Column
    private LocalDate lastMaintenance;

    @Column(nullable = false)
    private Integer repairCount = 0;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FuelType fuelType;

    @OneToMany(mappedBy = "bus")
    private List<Dispatch> dispatches = new ArrayList<>();

    // 필수값 생성자
    public Bus(Integer capacity, String vehicleNumber, VehicleType vehicleType, Integer vehicleYear, Operator operator, FuelType fuelType) {
        this.capacity = capacity;
        this.vehicleNumber = vehicleNumber;
        this.vehicleType = vehicleType;
        this.vehicleYear = vehicleYear;
        this.operator = operator;
        this.fuelType = fuelType;
    }

    // 연관관계 편의 메서드
    public void addDispatch(Dispatch dispatch) {
        this.dispatches.add(dispatch);
        if (dispatch.getBus() != this) {
            dispatch.setBus(this);
        }
    }

}
