package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.entity.Operator;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;


@Repository
public interface DriverRepository extends JpaRepository<Driver, Long> {
    List<Driver> findByOperator_OperatorId(Long operatorId);

    // 특정 운수회사(Operator 엔티티)의 모든 운전자 조회
    List<Driver> findAllByOperator(Operator operator);

    // 특정 운수회사 소속 운전자 중, 주어진 ID 목록에 포함되지 않은 운전자들 조회
    List<Driver> findByOperatorAndUserIdNotIn(Operator operator, List<Long> ids);
}