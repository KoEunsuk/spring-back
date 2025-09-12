package com.drive.backend.drive_api.dto;

import com.drive.backend.drive_api.enums.Role;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResponseDto { // 사용자 정보 응답용 (출력)
    private Long id;
    private String username;
    //private String email;
    private Role role;
}