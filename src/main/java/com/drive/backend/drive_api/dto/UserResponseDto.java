package com.drive.backend.drive_api.dto;

import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;

@Getter @Setter @NoArgsConstructor @AllArgsConstructor
public class UserResponseDto { // 사용자 정보 응답용 (출력)
    private Long id;
    private String username;
    private String email;
    private List<String> roles; // 사용자의 역할 목록
}