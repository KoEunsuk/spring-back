package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "admins")
@DiscriminatorValue("ADMIN")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Admin extends User {

    public Admin(String email, String password, String username, String phoneNumber, Operator operator) {
        super(email, password, username, phoneNumber, operator);
    }

    @Override
    public Role getRole() {
        return Role.ADMIN;
    }
}
