package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.entity.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {
    private String username;
    private String password;
    //private String email;
    private Role role;
}