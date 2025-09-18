package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // 상속: 조인 전략 사용
@DiscriminatorColumn(name = "role") // 자식 타입을 구분할 컬럼
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long userId;

    @Column(nullable = false, unique = true)
    private String email;   // 로그인 시 사용

    @Column(nullable = false)
    private String password;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false)
    private String phoneNumber;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private Operator operator;

    @Column
    private String imagePath;

    protected User(String email, String password, String username, String phoneNumber, Operator operator, String imagePath) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.operator = operator;
        this.imagePath = imagePath;
    }

    public abstract Role getRole();

}