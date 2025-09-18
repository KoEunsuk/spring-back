package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
// JpaRepository<[매핑할 엔티티 타입], [엔티티의 ID(기본 키) 타입]> 상속
public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByEmail(String email);
}