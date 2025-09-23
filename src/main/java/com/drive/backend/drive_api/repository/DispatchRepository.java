package com.drive.backend.drive_api.repository;

import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.enums.DispatchStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DispatchRepository extends JpaRepository<Dispatch, Long> {

    // 중복배차 방지를 위한 메서드
    @Query("SELECT d.bus.busId FROM Dispatch d WHERE d.status IN :statuses AND d.scheduledDepartureTime < :endTime AND d.scheduledArrivalTime > :startTime")
    List<Long> findDispatchedBusIdsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("statuses") List<DispatchStatus> statuses);

    @Query("SELECT d.driver.userId FROM Dispatch d WHERE d.status IN :statuses AND d.scheduledDepartureTime < :endTime AND d.scheduledArrivalTime > :startTime")
    List<Long> findDispatchedDriverIdsBetween(@Param("startTime") LocalDateTime startTime, @Param("endTime") LocalDateTime endTime, @Param("statuses") List<DispatchStatus> statuses);

    // 충돌 단일 검사 메서드
    boolean existsByBus_BusIdAndStatusInAndScheduledDepartureTimeBeforeAndScheduledArrivalTimeAfter(
            Long busId, List<DispatchStatus> statuses, LocalDateTime endTime, LocalDateTime startTime);
    boolean existsByDriver_UserIdAndStatusInAndScheduledDepartureTimeBeforeAndScheduledArrivalTimeAfter(
            Long driverId, List<DispatchStatus> statuses, LocalDateTime endTime, LocalDateTime startTime);

    // 운전자용 - 특정 운전자의 지정된 기간 내 배차 목록을 출발 시간 기준 오름차순으로 조회
    List<Dispatch> findAllByDriverUserIdAndScheduledDepartureTimeBetweenOrderByScheduledDepartureTimeAsc(
            Long driverId, LocalDateTime start, LocalDateTime end);

    // 관리자용 - 날짜 범위로 배차 조회
    List<Dispatch> findAllByBus_Operator_OperatorIdAndDispatchDateBetweenOrderByScheduledDepartureTimeAsc(
            Long operatorId, LocalDate startDate, LocalDate endDate);

    // 관리자용 - 날짜 범위 + 상태로 조회
    List<Dispatch> findAllByBus_Operator_OperatorIdAndDispatchDateBetweenAndStatusInOrderByScheduledDepartureTimeAsc(
            Long operatorId, LocalDate startDate, LocalDate endDate, List<DispatchStatus> statuses);

}
