package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Status;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor; // Lombok AllArgsConstructor 추가

import java.math.BigDecimal;

@Entity // 이 클래스가 JPA Entity임을 명시
@Table(name = "driver") // ERD의 driver 테이블과 매핑
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok 어노테이션
public class Driver {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "driver_id") // ERD 필드명에 맞춤 (driverId)
    private Long driverId;

    @Column(name = "driver_name", length = 100, nullable = false) // ERD 필드명에 맞춤
    private String driverName;

    @Column(name = "driver_password", length = 255, nullable = false) // ERD 필드명에 맞춤
    private String driverPassword; // 비밀번호, 나중에 User와 연동 시 사용 방식 재고려

    @Column(name = "license_number", length = 50) // ERD 필드명에 맞춤
    private String licenseNumber;

    @Column(name = "phone_number", length = 50) // ERD 필드명에 맞춤
    private String phoneNumber;

    @Column(name = "career_years", nullable = true) // ERD 필드명에 맞춤
    private Integer careerYears;

    @Column(name = "avg_driving_score", precision = 4, scale = 2) // ERD 필드명에 맞춤
    private BigDecimal avgDrivingScore;

    @Column(name = "grade", length = 10) // ERD 필드명에 맞춤
    private String grade;

    @Column(name = "driver_image_path", length = 255) // ERD 필드명에 맞춤
    private String driverImagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true) // nullable은 필요에 따라 설정
    private User user;
    // avgDrivingScore 추가 필요


}
