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

    // 특정 운수회사에 소속된 버스 목록 조회
    List<Bus> findAllByOperator_OperatorId(Long operatorId);

}
