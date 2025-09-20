package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Bus_old;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface BusRepository_old extends JpaRepository<Bus_old, Long> {

    Optional<Bus_old> findByVehicleNumber(String vehicleNumber); // 차량번호로 버스목록 찾기.
    List<Bus_old> findByRouteNumber(String routeNumber); // 노선번호로 버스목록 찾기.
    List<Bus_old> findByOperatorOperatorId(Long operatorId); // 특정 회사에 속한 버스 찾기.
}