package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Entity
@Table(name = "drivers")
public class Driver {

    // Getter / Setter
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String phone;
    private String status;

    public Driver() {}

    public Driver(String name, String phone, String status) {
        this.name = name;
        this.phone = phone;
        this.status = status;
    }

}
