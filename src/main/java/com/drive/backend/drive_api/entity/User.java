package com.drive.backend.drive_api.entity;



import com.drive.backend.drive_api.enums.Role;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "user")
@Getter @Setter @NoArgsConstructor //@AllArgsConstructor // Lombok
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false)
    private String password;

    //@Column -> 이메일은 일단 보류 아이디 비번만 가지고 기본만.
    //private String email;

    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    private Role role;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id")
    private Operator operator;

    // Admin과의 양방향 OneToOne 매핑 (선택적)
    // 이 User 계정이 어떤 Admin 프로필에 연결되어 있는지 명시.
    // 'mappedBy'는 연관관계의 주인이 아님을 의미하며, Admin 엔티티의 'user' 필드가 연관관계의 주인이 됨.
   // @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
   // private Admin adminProfile; // 이 필드를 통해 해당 User에 연결된 Admin 프로필을 조회 가능.
}