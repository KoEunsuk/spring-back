package com.drive.backend.drive_api.entity;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "operators")
@Getter @Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Operator {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long operatorId;

    @Column(nullable = false, unique = true)
    private String operatorCode;

    @Column(nullable = false)
    private String operatorName;

    @OneToMany(mappedBy = "operator", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<User> users = new ArrayList<>();

    // 연관관계 설정을 위한 편의 메서드 -> 사용하지 않을 경우, 양쪽 다 추가 잊지말기
    public void addUser(User user) {
        this.users.add(user);
        user.setOperator(this);
    }

}