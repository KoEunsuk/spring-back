package com.drive.backend.drive_api.entity;

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

    @Column(name = "operator_id", nullable = true) // ERD 필드명에 맞춤
    private Integer operatorId;

    @Column(name = "avg_driving_score", precision = 4, scale = 2) // ERD 필드명에 맞춤
    private BigDecimal avgDrivingScore;

    @Column(name = "grade", length = 10) // ERD 필드명에 맞춤
    private String grade;

    @Column(name = "driver_image_path", length = 255) // ERD 필드명에 맞춤
    private String driverImagePath;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    private Status status;

    // avgDrivingScore 추가 필요

}

    // Operator와의 다대일(ManyToOne) 관계
    // 여러 Driver가 한 Operator에 속함 (ERD의 operatorId FK)
    @ManyToOne(fetch = FetchType.LAZY) // 지연 로딩: Driver 로드 시 Operator를 즉시 로드하지 않고 필요할 때 로드
    @JoinColumn(name = "operator_id") // ERD의 operatorId 컬럼을 외래키로 사용
    private Operator operator; // 이 Driver가 속한 Operator 엔티티 객체

}
