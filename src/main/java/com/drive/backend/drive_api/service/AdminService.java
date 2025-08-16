package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.AdminCreateRequest;
import com.drive.backend.drive_api.dto.AdminResponseDto;
import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 처리를 위해

import java.util.ArrayList; // User roles를 List로 변환 시 사용

@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository; // User 계정 생성을 위해 필요
    private final OperatorRepository operatorRepository;
    private final AuthService authService; // User 계정 생성 비즈니스 로직 위임 (비밀번호 해싱 포함)

    public AdminService(AdminRepository adminRepository, UserRepository userRepository, OperatorRepository operatorRepository, AuthService authService) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        this.operatorRepository = operatorRepository;
        this.authService = authService;
    }

    // AdminCreateRequest -> AdminResponseDto 변환 헬퍼 (Service 내부 사용)
    private AdminResponseDto toAdminResponseDto(Admin admin) {
        // user와 operator는 지연 로딩될 수 있으므로, 프록시가 아닌 실제 데이터가 로딩되도록 강제하거나,
        // DTO를 만들 때 필요한 필드만 직접 설정 (강제 로딩)
        User user = admin.getUser(); // Lazy Loading 시 EAGER나 join fetch 필요
        Operator operator = admin.getOperator(); // Lazy Loading 시 EAGER나 join fetch 필요

        return new AdminResponseDto(
                admin.getAdminId(),
                admin.getAdminName(),
                user != null ? user.getId() : null,
                user != null ? user.getUsername() : null,
                user != null ? user.getEmail() : null,
                user != null ? new ArrayList<>(user.getRoles()) : new ArrayList<>(),
                operator != null ? operator.getOperatorId() : null,
                operator != null ? operator.getOperatorName() : null
        );
    }

    // 새로운 Admin(관리자 프로필) 생성 로직 ⭐
    @Transactional // 두 개 이상의 DB 작업(User 생성, Admin 생성)을 하나의 트랜잭션으로 묶어 원자성 보장
    public AdminResponseDto createAdminProfile(AdminCreateRequest request) {
        // 1. User 계정 생성 및 ROLE_ADMIN 부여 (AuthService 위임)
        UserDto userDto = new UserDto(request.getUsername(), request.getPassword(), request.getEmail());
        // registerAdmin이 ROLE_ADMIN을 부여하고 User를 저장.
        // 이때 반환되는 UserResponseDto는 응답용이고, 실제 Admin 엔티티에 연결할 User 객체가 필요.
        // AuthService에 User 엔티티를 직접 반환하는 registerAdminInternal 같은 메서드를 만들거나,
        // userRepository.findByUsername으로 방금 생성된 User를 다시 가져와야 함.
        // 현재 AuthService.registerAdmin이 UserResponseDto를 반환하므로, User 엔티티를 직접 가져오도록 수정 또는 편의 메서드 추가 필요.

        User user = authService.registerAdminAndGetUser(userDto); // AuthService에 추가될 메서드. User 반환.

        // 2. AdminProfile 생성
        Admin admin = new Admin();
        admin.setAdminName(request.getAdminName());
        admin.setUser(user); // User 엔티티와 연결

        // 3. Operator 연결
        Operator operator = operatorRepository.findById(request.getOperatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", request.getOperatorId()));
        admin.setOperator(operator);

        // 4. AdminProfile 저장
        Admin savedAdmin = adminRepository.save(admin);

        // 5. User 엔티티에 Admin 프로필 연결 (양방향 관계 유지 시)
        // user.setAdminProfile(savedAdmin); // User 엔티티에 @OneToOne mappedBy 설정 시, 여기서는 따로 저장할 필요 없음.
        // 영속성 전이(CascadeType.ALL) 사용 시 자동으로 처리되기도 함.
        // Admin.user가 연관관계 주인이므로 Admin 저장 시 User의 FK가 자동으로 업데이트 됨.

        // 6. 응답 DTO로 변환하여 반환
        return toAdminResponseDto(savedAdmin);
    }

    // ID로 Admin 조회
    public AdminResponseDto getAdminById(Integer adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));
        return toAdminResponseDto(admin);
    }

}
