package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.DispatchCreateRequest;
import com.drive.backend.drive_api.dto.response.BusDetailDto;
import com.drive.backend.drive_api.dto.response.DispatchDetailDto;
import com.drive.backend.drive_api.dto.response.DriverDetailDto;
import com.drive.backend.drive_api.entity.*;
import com.drive.backend.drive_api.enums.DispatchStatus;
import com.drive.backend.drive_api.enums.NotificationType;
import com.drive.backend.drive_api.exception.BusinessException;
import com.drive.backend.drive_api.exception.ErrorCode;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.BusRepository;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final BusRepository busRepository;
    private final DriverRepository driverRepository;
    private final NotificationService notificationService;
    private static final List<DispatchStatus> ACTIVE_STATUSES = List.of(DispatchStatus.SCHEDULED, DispatchStatus.RUNNING);

    // 관리자 - 신규 배차 생성
    public DispatchDetailDto createDispatch(DispatchCreateRequest createDto, CustomUserDetails currentUser) {
        Long operatorId = currentUser.getOperatorId();

        Bus bus = busRepository.findById(createDto.getBusId())
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", createDto.getBusId()));
        if (!bus.getOperator().getOperatorId().equals(operatorId)) {
            throw new AccessDeniedException("소속된 회사의 버스가 아닙니다.");
        }

        Driver driver = driverRepository.findById(createDto.getDriverId())
                .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", createDto.getDriverId()));
        if (!driver.getOperator().getOperatorId().equals(operatorId)) {
            throw new AccessDeniedException("소속된 회사의 운전자가 아닙니다.");
        }

        // 배차 충돌 최종 검사
        validateNoConflict(bus.getBusId(), driver.getUserId(), createDto.getScheduledDepartureTime(), createDto.getScheduledArrivalTime());

        Dispatch newDispatch = new Dispatch(bus, driver, createDto.getScheduledDepartureTime(), createDto.getScheduledArrivalTime());

        // 양방향 관계 동기화
        newDispatch.setDrivingRecord(new DrivingRecord(newDispatch));
        bus.addDispatch(newDispatch);
        driver.addDispatch(newDispatch);

        Dispatch savedDispatch = dispatchRepository.save(newDispatch);

        // 운전자에게 배차 할당 알림 전송
        String departureTime = savedDispatch.getScheduledDepartureTime()
                .format(DateTimeFormatter.ofPattern("M월 d일 HH:mm"));
        String message = String.format("새로운 배차가 할당되었습니다. (출발: %s)", departureTime);
        String url = "/dispatches/" + savedDispatch.getDispatchId();    // TODO
        notificationService.createAndSendNotification(
                driver,
                savedDispatch,
                message,
                NotificationType.NEW_DISPATCH_ASSIGNED,
                url
        );

        return DispatchDetailDto.from(savedDispatch);
    }

    // 관리자 - 배차 가능한 버스 목록 조회
    @Transactional(readOnly = true)
    public List<BusDetailDto> findAvailableBuses(LocalDateTime startTime, LocalDateTime endTime, CustomUserDetails currentUser) {
        Long operatorId = currentUser.getOperatorId();

        List<Long> dispatchedBusIds = dispatchRepository.findDispatchedBusIdsBetween(startTime, endTime, ACTIVE_STATUSES);
        List<Bus> availableBuses;
        if (dispatchedBusIds.isEmpty()) {
            availableBuses = busRepository.findAllByOperator_OperatorId(operatorId);
        } else {
            availableBuses = busRepository.findByOperator_OperatorIdAndBusIdNotIn(operatorId, dispatchedBusIds);
        }
        return availableBuses.stream().map(BusDetailDto::from).collect(Collectors.toList());
    }

    // 관리자 - 배차 가능한 운전자 목록 조회
    @Transactional(readOnly = true)
    public List<DriverDetailDto> findAvailableDrivers(LocalDateTime startTime, LocalDateTime endTime, CustomUserDetails currentUser) {
        Long operatorId = currentUser.getOperatorId();

        List<Long> dispatchedDriverIds = dispatchRepository.findDispatchedDriverIdsBetween(startTime, endTime, ACTIVE_STATUSES);
        List<Driver> availableDrivers;
        if (dispatchedDriverIds.isEmpty()) {
            availableDrivers = driverRepository.findAllByOperator_OperatorId(operatorId);
        } else {
            availableDrivers = driverRepository.findByOperator_OperatorIdAndUserIdNotIn(operatorId, dispatchedDriverIds);
        }
        return availableDrivers.stream().map(DriverDetailDto::from).collect(Collectors.toList());
    }

    // 공통 - 특정 배차 상세 조회
    @Transactional(readOnly = true)
    public DispatchDetailDto getDispatchById(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = findAndCheckPermission(dispatchId, currentUser);
        return DispatchDetailDto.from(dispatch);
    }

    // 관리자 - 배차 목록 조회 by 날짜, 상태
    @Transactional(readOnly = true)
    public List<DispatchDetailDto> getDispatchesForAdmin(LocalDate startDate, LocalDate endDate, List<DispatchStatus> statuses, CustomUserDetails currentUser) {
        Long operatorId = currentUser.getOperatorId();

        List<Dispatch> dispatches;

        // statuses 파라미터가 유효한지 확인
        if (statuses != null && !statuses.isEmpty()) {
            // 상태 필터가 있는 경우
            dispatches = dispatchRepository.findAllByBus_Operator_OperatorIdAndDispatchDateBetweenAndStatusInOrderByScheduledDepartureTimeAsc(
                    operatorId, startDate, endDate, statuses);
        } else {
            // 상태 필터가 없는 경우 (전체 조회)
            dispatches = dispatchRepository.findAllByBus_Operator_OperatorIdAndDispatchDateBetweenOrderByScheduledDepartureTimeAsc(
                    operatorId, startDate, endDate);
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

    // 공통 - 배차 운행 시작
    public DispatchDetailDto startDispatch(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = findAndCheckPermission(dispatchId, currentUser);

        if (dispatch.getStatus() != DispatchStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.DISPATCH_STATUS_NOT_SCHEDULED);
        }

        dispatch.setStatus(DispatchStatus.RUNNING);
        dispatch.setActualDepartureTime(LocalDateTime.now());

        return DispatchDetailDto.from(dispatch);
    }

    // 공통 - 배차 운행 종료
    public DispatchDetailDto endDispatch(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = findAndCheckPermission(dispatchId, currentUser);

        if (dispatch.getStatus() != DispatchStatus.RUNNING) {
            throw new BusinessException(ErrorCode.DISPATCH_NOT_IN_RUNNING_STATE);
        }

        dispatch.setStatus(DispatchStatus.COMPLETED);
        dispatch.setActualArrivalTime(LocalDateTime.now());

        return DispatchDetailDto.from(dispatch);
    }

    // 관리자 - 배차 취소
    public DispatchDetailDto cancelDispatch(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = findAndCheckPermission(dispatchId, currentUser);

        if (!isAdmin(currentUser)) {
            throw new AccessDeniedException("배차 취소는 관리자만 가능합니다.");
        }

        if (dispatch.getStatus() != DispatchStatus.SCHEDULED) {
            throw new BusinessException(ErrorCode.DISPATCH_ALREADY_STARTED);
        }

        dispatch.setStatus(DispatchStatus.CANCELED);
        return DispatchDetailDto.from(dispatch);
    }

    // 배차 조회 및 역할 기반 권한 검사 헬퍼 메서드
    private Dispatch findAndCheckPermission(Long dispatchId, CustomUserDetails currentUser) {
        Dispatch dispatch = dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", dispatchId));

        if (isAdmin(currentUser)) {
            if (!dispatch.getBus().getOperator().getOperatorId().equals(currentUser.getOperatorId())) {
                throw new AccessDeniedException("소속된 회사의 배차가 아닙니다.");
            }
        } else { // DRIVER
            if (!dispatch.getDriver().getUserId().equals(currentUser.getUserId())) {
                throw new AccessDeniedException("본인에게 할당된 배차가 아닙니다.");
            }
        }
        return dispatch;
    }

    // 충돌 검사 헬퍼 메서드
    private void validateNoConflict(Long busId, Long driverId, LocalDateTime startTime, LocalDateTime endTime) {
        if (dispatchRepository.existsByBus_BusIdAndStatusInAndScheduledDepartureTimeBeforeAndScheduledArrivalTimeAfter(
                busId, ACTIVE_STATUSES, endTime, startTime)) {
            throw new BusinessException(ErrorCode.BUS_ALREADY_DISPATCHED);
        }
        if (dispatchRepository.existsByDriver_UserIdAndStatusInAndScheduledDepartureTimeBeforeAndScheduledArrivalTimeAfter(
                driverId, ACTIVE_STATUSES, endTime, startTime)) {
            throw new BusinessException(ErrorCode.DRIVER_ALREADY_DISPATCHED);
        }
    }

    // 사용자가 관리자인지 확인하는 헬퍼 메서드
    private boolean isAdmin(CustomUserDetails currentUser) {
        return currentUser.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN"));
    }
}
