package com.drive.backend.drive_api.entity;

import com.drive.backend.drive_api.enums.Role;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "users")
@Inheritance(strategy = InheritanceType.JOINED) // 상속: 조인 전략 사용
@DiscriminatorColumn(name = "role") // 자식 타입을 구분할 컬럼
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public abstract class User {

    @Version
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    private Long version;

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

    @Column
    private Instant passwordChangedAt;

    @OneToMany(mappedBy = "recipient", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Notification> notifications = new ArrayList<>();

    // 필수정보 누락 방지
    protected User(String email, String password, String username, String phoneNumber, Operator operator) {
        this.email = email;
        this.password = password;
        this.username = username;
        this.phoneNumber = phoneNumber;
        this.operator = operator;
    }

    public abstract Role getRole();

    // 연관관계 편의 메서드
    public void addNotification(Notification notification) {
        this.notifications.add(notification);
        if (notification.getRecipient() != this) {
            notification.setRecipient(this);
        }
    }

}