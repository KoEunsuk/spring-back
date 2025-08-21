package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.JwtResponse;
import com.drive.backend.drive_api.dto.LoginRequest;
import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.dto.UserResponseDto;
import com.drive.backend.drive_api.entity.Role;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 처리를 위해


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // UserDto (입력) -> User 엔티티 변환 헬퍼
    private User toEntity(UserDto dto) {
        User user = new User();
        user.setUsername(dto.getUsername());
        user.setPassword(dto.getPassword());
        user.setRole(dto.getRole());
        //user.setEmail(dto.getEmail());
        return user;
    }

    // User 엔티티 -> UserResponseDto (출력) 변환 헬퍼
    private UserResponseDto toResponseDto(User entity) {
        return new UserResponseDto(
                entity.getId(),
                entity.getUsername(),
                entity.getRole()
                //entity.getEmail(),
        );
    }

    // 새로운 사용자 (일반 유저) 회원가입
    @Transactional
    public UserResponseDto registerUser(UserDto userDto) {
        if (userRepository.findByUsername(userDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 사용자 이름입니다.");
        }

        User user = toEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        user.setRole(Role.DRIVER);

        User savedUser = userRepository.save(user);
        return toResponseDto(savedUser);
    }

    // AuthController가 호출할, UserResponseDto를 반환하는 registerAdmin 메서드
    @Transactional
    public UserResponseDto registerAdmin(UserDto adminDto) {
        // 내부적으로 User 엔티티를 반환하는 registerAdminAndGetUser 메서드를 호출
        User adminUser = registerAdminAndGetUser(adminDto);
        return toResponseDto(adminUser); // UserResponseDto로 변환하여 반환
    }

    // AdminService가 Admin 프로필 생성 시 호출할, User 엔티티를 반환하는 메서드
    // 이 메서드는 AuthService 내부에서 사용되거나, AdminService와 같이 User 엔티티가 필요한 경우 호출됨.
    @Transactional
    public User registerAdminAndGetUser(UserDto adminDto) {
        if (userRepository.findByUsername(adminDto.getUsername()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 관리자 이름입니다.");
        }

        User adminUser = toEntity(adminDto);
        adminUser.setPassword(passwordEncoder.encode(adminUser.getPassword()));
        adminUser.setRole(Role.ADMIN);

        return userRepository.save(adminUser); // User 엔티티 자체를 반환
    }


    // 사용자 로그인 및 인증
    public JwtResponse authenticateUser(LoginRequest loginRequest) {
        User user = userRepository.findByUsername(loginRequest.getUsername())
                .orElseThrow(() -> new ResourceNotFoundException("User", "username", loginRequest.getUsername()));

        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("비밀번호가 일치하지 않습니다.");
        }

        String jwtToken = "mock_jwt_token_for_" + loginRequest.getUsername();
        return  new JwtResponse(jwtToken, user.getUsername(), Collections.singletonList(user.getRole().name()));
    }
}