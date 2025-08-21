package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdminCreateRequest {
    private String username; // User 엔티티에 연결될 username (Admin의 계정 ID)
    private String password; // User 엔티티에 연결될 password
    //private String email;    // User 엔티티에 연결될 email
    private String adminName; // Admin 엔티티의 adminName (ERD 필드)
    //private Integer operatorId; // Admin 엔티티가 연결될 Operator의 ID
}