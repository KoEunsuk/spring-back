package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Bus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository extends JpaRepository<Bus, Long> {

    // 차량 번호를 이용해 버스 조회
    Optional<Bus> findByVehicleNumber(String vehicleNumber);

    // 특정 운수회사 ID로 소속 버스 목록 조회
    List<Bus> findAllByOperator_OperatorId(Long operatorId);

    // 특정 운수회사 소속 버스 중, 주어진 ID 목록에 포함되지 않은 버스들 조회
    List<Bus> findByOperator_OperatorIdAndBusIdNotIn(Long operatorId, List<Long> ids);

}
