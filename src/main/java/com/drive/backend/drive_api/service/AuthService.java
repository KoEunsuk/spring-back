package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.*;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 처리를 위해


@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, AdminRepository adminRepository, DriverRepository driverRepository, OperatorRepository operatorRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.adminRepository = adminRepository;
        this.driverRepository = driverRepository;
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public void signup(SignupRequestDto signupDto){
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new RuntimeException("이미 사용중인 이메일입니다.");
        }

        Operator operator = operatorRepository.findByOperatorCode(signupDto.getOperatorCode())
                .orElseThrow(() -> new RuntimeException("운수사를 찾을 수 없습니다."));

        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());

        if (signupDto.getRole() == Role.ADMIN) {
            Admin admin = new Admin(
                    signupDto.getEmail(),
                    encodedPassword,
                    signupDto.getUsername(),
                    signupDto.getPhoneNumber(),
                    operator,
                    signupDto.getImagePath()
            );
            adminRepository.save(admin);
        } else if (signupDto.getRole() == Role.DRIVER) {
            Driver driver = new Driver(
                    signupDto.getEmail(),
                    encodedPassword,
                    signupDto.getUsername(),
                    signupDto.getPhoneNumber(),
                    operator,
                    signupDto.getImagePath(),
                    signupDto.getLicenseNumber(),
                    signupDto.getCareerYears()
            );
            driverRepository.save(driver);
        } else {
            throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }
    }
}