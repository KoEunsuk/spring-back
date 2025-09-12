package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.AdminCreateRequest;
import com.drive.backend.drive_api.dto.AdminResponseDto;
import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.entity.Admin;
import com.drive.backend.drive_api.enums.Role;
import com.drive.backend.drive_api.entity.User;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.AdminRepository;
import com.drive.backend.drive_api.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional; // 트랜잭션 처리를 위해
@Service
public class AdminService {

    private final AdminRepository adminRepository;
    private final UserRepository userRepository; // User 계정 생성을 위해 필요
    //private final OperatorRepository operatorRepository;
    private final AuthService authService; // User 계정 생성 비즈니스 로직 위임 (비밀번호 해싱 포함)

    public AdminService(AdminRepository adminRepository, UserRepository userRepository, AuthService authService) {
        this.adminRepository = adminRepository;
        this.userRepository = userRepository;
        //this.operatorRepository = operatorRepository;
        this.authService = authService;
    }

    // AdminCreateRequest -> AdminResponseDto 변환 헬퍼 (Service 내부 사용)
    private AdminResponseDto toAdminResponseDto(Admin admin) {
        // user와 operator는 지연 로딩될 수 있으므로, 프록시가 아닌 실제 데이터가 로딩되도록 강제하거나,
        // DTO를 만들 때 필요한 필드만 직접 설정 (강제 로딩)
        User user = admin.getUser(); // Lazy Loading 시 EAGER나 join fetch 필요
        //Operator operator = admin.getOperator(); // Lazy Loading 시 EAGER나 join fetch 필요

        return new AdminResponseDto(
                admin.getAdminId(),
                admin.getAdminName(),
                user != null ? user.getId() : null,
                user != null ? user.getUsername() : null,
                //user != null ? user.getEmail() : null,
                user != null ? user.getRole() : null
                //operator != null ? operator.getOperatorId() : null,
                //operator != null ? operator.getOperatorName() : null
        );
    }

    // 새로운 Admin(관리자 프로필) 생성 로직 ⭐
    @Transactional // 두 개 이상의 DB 작업(User 생성, Admin 생성)을 하나의 트랜잭션으로 묶어 원자성 보장
    public AdminResponseDto createAdminProfile(AdminCreateRequest request) {
        UserDto userDtoForAdmin = new UserDto(request.getUsername(), request.getPassword(), Role.ADMIN);
        User user = authService.registerAdminAndGetUser(userDtoForAdmin);

        Admin admin = new Admin();
        admin.setAdminName(request.getAdminName());
        admin.setUser(user);

        Admin savedAdmin = adminRepository.save(admin);

        return toAdminResponseDto(savedAdmin);
    }

    // ID로 Admin 조회
    public AdminResponseDto getAdminById(Long adminId) {
        Admin admin = adminRepository.findById(adminId)
                .orElseThrow(() -> new ResourceNotFoundException("Admin", "id", adminId));
        return toAdminResponseDto(admin);
    }

}
