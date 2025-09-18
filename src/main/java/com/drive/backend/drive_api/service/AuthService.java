package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;


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

    @Transactional
    public LoginResponseDto signup(SignupRequestDto signupDto){
        if (userRepository.findByEmail(signupDto.getEmail()).isPresent()) {
            throw new IllegalArgumentException("이미 사용 중인 이메일입니다.");
        }

        Operator operator = operatorRepository.findByOperatorCode(signupDto.getOperatorCode())
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 운수사 코드입니다."));

        String encodedPassword = passwordEncoder.encode(signupDto.getPassword());

        User newUser;
        if (signupDto.getRole() == Role.ADMIN) {
            Admin admin = new Admin(
                    signupDto.getEmail(),
                    encodedPassword,
                    signupDto.getUsername(),
                    signupDto.getPhoneNumber(),
                    operator,
                    signupDto.getImagePath()
            );
            newUser = adminRepository.save(admin);
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
            newUser = driverRepository.save(driver);
        } else {
            throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }

        return LoginResponseDto.from(newUser);
    }
}