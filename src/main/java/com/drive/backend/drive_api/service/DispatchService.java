package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.DispatchCreateRequest;
import com.drive.backend.drive_api.dto.response.BusDetailDto;
import com.drive.backend.drive_api.dto.response.DispatchDetailDto;
import com.drive.backend.drive_api.dto.response.DriverDetailDto;
import com.drive.backend.drive_api.entity.*;
import com.drive.backend.drive_api.enums.DispatchStatus;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.BusRepository;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.security.SecurityUtil;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final OperatorRepository operatorRepository;
    private static final List<DispatchStatus> ACTIVE_STATUSES = List.of(DispatchStatus.SCHEDULED, DispatchStatus.RUNNING);

    // 관리자 - 신규 배차 생성
    public DispatchDetailDto createDispatch(DispatchCreateRequest createDto) {
        Bus bus = busRepository.findById(createDto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", createDto.getBusId()));
        Driver driver = driverRepository.findById(createDto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", createDto.getDriverId()));

        // 배차 충돌 최종 검사
        validateNoConflict(bus.getBusId(), driver.getUserId(), createDto.getScheduledDepartureTime(), createDto.getScheduledArrivalTime());

        Dispatch newDispatch = new Dispatch(bus, driver, createDto.getScheduledDepartureTime(), createDto.getScheduledArrivalTime());

        // 운행 기록 엔티티도 함께 생성
        DrivingRecord drivingRecord = new DrivingRecord(newDispatch);
        newDispatch.setDrivingRecord(drivingRecord);

        // 양방향 관계 동기화
        bus.addDispatch(newDispatch);
        driver.addDispatch(newDispatch);

        return DispatchDetailDto.from(dispatchRepository.save(newDispatch));
    }

    // 관리자 - 배차 가능한 버스 목록 조회
    @Transactional(readOnly = true)
    public List<BusDetailDto> findAvailableBuses(LocalDateTime startTime, LocalDateTime endTime) {
        List<Long> dispatchedBusIds = dispatchRepository.findDispatchedBusIdsBetween(startTime, endTime, ACTIVE_STATUSES);
        List<Bus> availableBuses;
        if (dispatchedBusIds.isEmpty()) {
            availableBuses = busRepository.findAll();
        } else {
            availableBuses = busRepository.findByBusIdNotIn(dispatchedBusIds);
        }
        return availableBuses.stream().map(BusDetailDto::from).collect(Collectors.toList());
    }

    // 관리자 - 배차 가능한 운전자 목록 조회
    @Transactional(readOnly = true)
    public List<DriverDetailDto> findAvailableDrivers(LocalDateTime startTime, LocalDateTime endTime) {
        List<Long> dispatchedDriverIds = dispatchRepository.findDispatchedDriverIdsBetween(startTime, endTime, ACTIVE_STATUSES);
        List<Driver> availableDrivers;
        if (dispatchedDriverIds.isEmpty()) {
            availableDrivers = driverRepository.findAll();
        } else {
            availableDrivers = driverRepository.findByUserIdNotIn(dispatchedDriverIds);
        }
        return availableDrivers.stream().map(DriverDetailDto::from).collect(Collectors.toList());
    }

    // 관리자 - 특정 배차 상세 조회
    @Transactional(readOnly = true)
    public DispatchDetailDto getDispatchById(Long dispatchId) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));
        return DispatchDetailDto.from(dispatch);
    }

    // 관리자 - 배차 목록 조회 by 날짜, 상태
    @Transactional(readOnly = true)
    public List<DispatchDetailDto> getDispatchesForAdmin(LocalDate startDate, LocalDate endDate, List<DispatchStatus> statuses) {
        List<Dispatch> dispatches;

        // statuses 파라미터가 유효한지 확인
        if (statuses != null && !statuses.isEmpty()) {
            // 상태 필터가 있는 경우
            dispatches = dispatchRepository.findAllByDispatchDateBetweenAndStatusInOrderByScheduledDepartureTimeAsc(
                    startDate, endDate, statuses);
        } else {
            // 상태 필터가 없는 경우 (전체 조회)
            dispatches = dispatchRepository.findAllByDispatchDateBetweenOrderByScheduledDepartureTimeAsc(
                    startDate, endDate);
        }

        return dispatches.stream()
                .map(DispatchDetailDto::from)
                .collect(Collectors.toList());
    }

    // 운전자 - 지정된 기간 내 배차 목록 조회
    @Transactional(readOnly = true)
    public List<DispatchDetailDto> getDispatchesForDriverByDateRange(
            CustomUserDetails currentUser, LocalDate startDate, LocalDate endDate) {

        LocalDateTime startDateTime = startDate.atStartOfDay();
        LocalDateTime endDateTime = endDate.atTime(LocalTime.MAX);

        List<Dispatch> dispatches = dispatchRepository.findAllByDriverUserIdAndScheduledDepartureTimeBetweenOrderByScheduledDepartureTimeAsc(
                currentUser.getUserId(), startDateTime, endDateTime);

        return dispatches.stream()
                .map(DispatchDetailDto::from)
                .collect(Collectors.toList());
    }

    // 운전자 - 특정 배차 조회
    @Transactional(readOnly = true)
    public DispatchDetailDto getDispatchByIdForDriver(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        // 권한 검사: 이 배차가 정말 내 것이 맞나?
        if (!dispatch.getDriver().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("자신에게 할당된 배차가 아닙니다.");
        }

        return DispatchDetailDto.from(dispatch);
    }

    // 관리자 - 배차 운행 시작
    public DispatchDetailDto startDispatch(Long dispatchId) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        if (dispatch.getStatus() != DispatchStatus.SCHEDULED) {
            throw new IllegalStateException("'배차 예정' 상태인 배차만 운행을 시작할 수 있습니다.");
        }

        dispatch.setStatus(DispatchStatus.RUNNING);
        dispatch.setActualDepartureTime(LocalDateTime.now());

        return DispatchDetailDto.from(dispatch);
    }

    // 운전자 - 배차 운행 시작
    public DispatchDetailDto startDispatch(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        // 운전자 본인인지 권한 검사
        if (!dispatch.getDriver().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("자신에게 할당된 배차가 아닙니다.");
        }

        // 권한 검사 통과 후, 파라미터 없는 startDispatch 메서드 호출
        return this.startDispatch(dispatchId);
    }

    // 관리자 - 배차 운행 종료
    public DispatchDetailDto endDispatch(Long dispatchId) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        if (dispatch.getStatus() != DispatchStatus.RUNNING) {
            throw new IllegalStateException("'운행 중' 상태인 배차만 운행을 종료할 수 있습니다.");
        }

        dispatch.setStatus(DispatchStatus.COMPLETED);
        dispatch.setActualArrivalTime(LocalDateTime.now());

        return DispatchDetailDto.from(dispatch);
    }

    // 운전자 - 배차 운행 종료
    public DispatchDetailDto endDispatch(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        if (!dispatch.getDriver().getUserId().equals(currentUser.getUserId())) {
            throw new AccessDeniedException("자신에게 할당된 배차가 아닙니다.");
        }

        return this.endDispatch(dispatchId);
    }

    // 관리자 - 배차 취소
    public DispatchDetailDto cancelDispatch(Long dispatchId) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        if (dispatch.getStatus() != DispatchStatus.SCHEDULED) {
            throw new IllegalStateException("이미 시작된 배차는 취소할 수 없습니다.");
        }

        dispatch.setStatus(DispatchStatus.CANCELED);
        return DispatchDetailDto.from(dispatch);
    }

    // 충돌 검사 헬퍼 메서드
    private void validateNoConflict(Long busId, Long driverId, LocalDateTime startTime, LocalDateTime endTime) {
        if (!dispatchRepository.findDispatchedBusIdsBetween(startTime, endTime, ACTIVE_STATUSES).isEmpty()) {
            throw new IllegalStateException("해당 버스는 요청된 시간에 이미 다른 배차가 있습니다.");
        }
        if (!dispatchRepository.findDispatchedDriverIdsBetween(startTime, endTime, ACTIVE_STATUSES).isEmpty()) {
            throw new IllegalStateException("해당 운전자는 요청된 시간에 이미 다른 배차가 있습니다.");
        }
    }
}
