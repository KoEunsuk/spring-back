package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List; // User의 roles를 List로 받음

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class AdminResponseDto {
    private Integer adminId;
    private String adminName;
    private Long userId; // 연결된 User의 ID
    private String username; // 연결된 User의 username
    private String email; // 연결된 User의 email
    private List<String> userRoles; // 연결된 User의 역할 목록
    private Integer operatorId; // 연결된 Operator의 ID
    private String operatorName; // 연결된 Operator의 이름
}