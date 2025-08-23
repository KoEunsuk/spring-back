package com.drive.backend.drive_api.entity;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "bus")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Bus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "bus_id")
    private Long busId; //

    @Column(name = "route_number", length = 50, nullable = false)
    private String routeNumber;

    @Column(name = "route_type", length = 10)
    private String routeType;

    private Integer capacity;

    @Column(name = "vehicle_number", length = 20, unique = true, nullable = false)
    private String vehicleNumber;

    @Column(name = "vehicle_type", length = 50)
    private String vehicleType;

    @Column(name = "vehicle_year")
    private Integer vehicleYear;

    @Column(name = "last_maintenance")
    private LocalDate lastMaintenance;

    @Column(name = "repair_count")
    private Integer repairCount;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false) // FK인 operator_id도 Long 값 참조.
    private Operator operator;
}
