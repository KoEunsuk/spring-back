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
    @Column(name = "admin_id")
    private Long adminId;

    @Column(name = "admin_name", length = 50, nullable = false)
    private String adminName;


    // User와의 1:1 관계는 그대로 유지
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

}


/** Opreator부분은 일단 보류.
    @ManyToOne(fetch = FetchType.LAZY) // ERD의 operatorId 외래키 참조
    @JoinColumn(name = "operator_id") // operator_id를 외래키로 사용
    private Operator operator; // 이 Admin이 속한 Operator 정보
**/

