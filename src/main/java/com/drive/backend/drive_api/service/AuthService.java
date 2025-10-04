package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.SignupRequest;
import com.drive.backend.drive_api.dto.response.SignupResponseDto;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public SignupResponseDto signup(SignupRequest signupDto){
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
                    operator
            );
            if (signupDto.getImagePath() != null) {
                admin.setImagePath(signupDto.getImagePath());
            }

            operator.addUser(admin);
            newUser = adminRepository.save(admin);
        } else if (signupDto.getRole() == Role.DRIVER) {
            Driver driver = new Driver(
                    signupDto.getEmail(),
                    encodedPassword,
                    signupDto.getUsername(),
                    signupDto.getPhoneNumber(),
                    operator,
                    signupDto.getLicenseNumber()
            );
            if (signupDto.getImagePath() != null) {
                driver.setImagePath(signupDto.getImagePath());
            }
            if (signupDto.getCareerYears() != null) {
                driver.setCareerYears(signupDto.getCareerYears());
            }

            operator.addUser(driver);
            newUser = driverRepository.save(driver);
        } else {
            throw new IllegalArgumentException("유효하지 않은 역할입니다.");
        }

        return SignupResponseDto.from(newUser);
    }
}