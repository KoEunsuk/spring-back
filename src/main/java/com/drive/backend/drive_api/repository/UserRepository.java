package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.dto.UserDto;
import com.drive.backend.drive_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;


@Repository
// JpaRepository<[매핑할 엔티티 타입], [엔티티의 ID(기본 키) 타입]> 상속
public interface UserRepository extends JpaRepository<User, Long> {
    // JpaRepository가 기본적으로 findAll(), findById(), save(), deleteById() 등 제공

    // 사용자 이름으로 조회하는 메서드는 JPA가 자동으로 구현해준다고함
    Optional<User> findByUsername(String username);
}