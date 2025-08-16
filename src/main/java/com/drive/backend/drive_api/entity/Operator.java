package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.List;
@Entity
@Table(name = "operator")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "operator_id")
    private Integer operatorId;

    @Column(name = "operator_code", length = 20, nullable = false, unique = true)
    private String operatorCode;

    @Column(name = "operator_name", length = 100, nullable = false)
    private String operatorName;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Admin> admins;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Driver> drivers;
}