package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Dispatch;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

//    List<Dispatch> findByStatus(String status); // 상태로 배차 목록 찾기
    List<Dispatch> findByDispatchDate(LocalDate dispatchDate); // 날짜로 배차 목록 찾기
    List<Dispatch> findByDriverUserId(Long userId); // 운전자 ID로 배차 목록 찾기
    List<Dispatch> findByBusBusId(Long busId); // 버스 ID로 배차 목록 찾기
}