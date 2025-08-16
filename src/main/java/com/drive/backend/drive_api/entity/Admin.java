package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Entity
@Table(name = "admin") // ERD의 admin 테이블과 매핑
@Getter @Setter @NoArgsConstructor @AllArgsConstructor // Lombok 어노테이션
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "admin_id") // ERD 필드명에 맞춤
    private Integer adminId;

    @Column(name = "admin_name", length = 50, nullable = false) // ERD의 adminName
    private String adminName;

    // 중요: adminPassword는 User 엔티티에 위임. 여기서는 User와의 1:1 관계만 정의한다.
    @OneToOne(fetch = FetchType.LAZY) // Admin 하나는 User 하나와 매핑
    @JoinColumn(name = "user_id", unique = true, nullable = false) // User 테이블의 PK(id)를 외래키로 참조
    private User user; // 이 Admin 프로필에 해당하는 User 계정 정보

    @ManyToOne(fetch = FetchType.LAZY) // ERD의 operatorId 외래키 참조
    @JoinColumn(name = "operator_id") // operator_id를 외래키로 사용
    private Operator operator; // 이 Admin이 속한 Operator 정보
}
