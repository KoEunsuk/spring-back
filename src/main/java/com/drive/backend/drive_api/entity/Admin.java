package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Role;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

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
