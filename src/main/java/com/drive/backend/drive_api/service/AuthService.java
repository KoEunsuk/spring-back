package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.LoginRequest;
import com.drive.backend.drive_api.dto.request.SignupRequest;
import com.drive.backend.drive_api.dto.response.SignupResponse;
import com.drive.backend.drive_api.entity.*;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.exception.TokenRefreshException;
import com.drive.backend.drive_api.repository.*;
import com.drive.backend.drive_api.security.jwt.JwtTokenProvider;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;


@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {

    private final UserRepository userRepository;
    private final AdminRepository adminRepository;
    private final DriverRepository driverRepository;
    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final RefreshTokenRepository refreshTokenRepository;


    // 두 토큰을 함께 반환하기 위한 레코드
    public record TokenInfo(String accessToken, String refreshToken) {}

    public SignupResponse signup(SignupRequest signupDto){
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

        return SignupResponse.from(newUser);
    }

    // 로그인
    public TokenInfo login(LoginRequest loginRequest) {
        // 1. 사용자 인증
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

        // 2. SecurityContext에 인증 정보 저장
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // 3. Access Token과 Refresh Token 생성
        String accessToken = jwtTokenProvider.generateAccessToken(authentication);
        String refreshTokenString = jwtTokenProvider.generateRefreshToken(authentication);

        // 4. Refresh Token 저장
        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
        Instant expiryDate = jwtTokenProvider.getExpiryDateFromToken(refreshTokenString);

        String hashedRefreshTokenString = passwordEncoder.encode(refreshTokenString);

        // 기존에 Refresh Token이 있었다면 삭제
        refreshTokenRepository.deleteByUser_UserId(userDetails.getUserId());

        // 새로 생성한 Refresh Token을 엔티티로 만들어 DB에 저장
        RefreshToken refreshToken = new RefreshToken(
                userDetails.getUserObject(),
                hashedRefreshTokenString,
                expiryDate
        );
        refreshTokenRepository.save(refreshToken);

        return new TokenInfo(accessToken, refreshTokenString);
    }

    // 로그아웃
    public void logout(String refreshTokenString, Authentication authentication) {
        if (refreshTokenString == null || refreshTokenString.isEmpty()) {
            return;
        }

        CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();


        // 사용자 ID로 토큰을 찾아 삭제
        refreshTokenRepository.findByUser_UserId(userDetails.getUserId())
                .ifPresent(refreshToken -> {
                    // (선택적 강화) 전달된 토큰과 DB 토큰이 일치하는지 최종 확인
                    if (passwordEncoder.matches(refreshTokenString, refreshToken.getToken())) {
                        refreshTokenRepository.delete(refreshToken);
                    }
                });
    }

    // RefreshToken을 이용한 AccessToken 재발급
    public String refreshAccessToken(String refreshTokenString) {
        if (refreshTokenString == null || refreshTokenString.isEmpty()) {
            throw new TokenRefreshException(null, "Refresh Token이 비어있습니다.");
        }

        // 1. (검증 없이) 토큰에서 사용자 이메일을 먼저 추출
        String email = jwtTokenProvider.getEmailFromToken(refreshTokenString);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new TokenRefreshException(refreshTokenString, "토큰의 사용자 정보를 찾을 수 없습니다."));

        // 2. 사용자 ID로 DB에 저장된 Refresh Token을 조회
        RefreshToken refreshToken = refreshTokenRepository.findByUser_UserId(user.getUserId())
                .orElseThrow(() -> new TokenRefreshException(refreshTokenString, "저장된 Refresh Token이 없습니다."));

        // 3. 토큰 만료 여부 확인
        if (refreshToken.getExpiryDate().isBefore(Instant.now())) {
            refreshTokenRepository.delete(refreshToken);
            throw new TokenRefreshException(refreshTokenString, "Refresh Token이 만료되었습니다. 다시 로그인해주세요.");
        }

        // 4. [중요] 전달받은 토큰과 DB의 토큰이 일치하는지 확인 (해시 비교)
        if (!passwordEncoder.matches(refreshTokenString, refreshToken.getToken())) {
            throw new TokenRefreshException(refreshTokenString, "Refresh Token이 일치하지 않습니다.");
        }

        // 5. 모든 검증 통과 시, 새로운 Access Token 생성
        CustomUserDetails userDetails = CustomUserDetails.build(user);
        Authentication authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

        return jwtTokenProvider.generateAccessToken(authentication);
    }
}