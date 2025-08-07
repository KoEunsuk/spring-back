package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.JwtResponse;
import com.drive.backend.drive_api.dto.LoginRequest;
import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public UserDto registerUser(UserDto userDto) {

        // 중복 사용자 이름 체크 로직 추가
        Optional<UserDto> existingUser = userRepository.findByUsername(userDto.getUsername());
        if (existingUser.isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }
        return userRepository.save(userDto);
    }

    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        // 실제로는 여기서 DB에서 사용자 조회 후 비밀번호 일치 여부 확인해야함.
        UserDto user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", loginRequest.getUsername()));

        // 실제로는 비밀번호 일치 여부 확인 (PasswordEncoder.matches 사용) 해야함.
        if (!user.getPassword().equals(loginRequest.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        // 인증 성공 시 JWT 토큰 생성 및 반환 로직 추가
        String jwtToken = "mock_jwt_token_for_" + loginRequest.getUsername(); // 임시 토큰
        return new JwtResponse(jwtToken, loginRequest.getUsername());
    }
}