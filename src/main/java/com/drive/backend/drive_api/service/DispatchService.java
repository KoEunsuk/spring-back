package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.DispatchDetailDto;
import com.drive.backend.drive_api.dto.DispatchDto;
import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Dispatch;
import com.drive.backend.drive_api.entity.Driver;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.BusRepository;
import com.drive.backend.drive_api.repository.DispatchRepository;
import com.drive.backend.drive_api.repository.DriverRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class DispatchService {

    private final DispatchRepository dispatchRepository;
    private final DriverRepository driverRepository;
    private final BusRepository busRepository;

    public DispatchService(DispatchRepository dispatchRepository, DriverRepository driverRepository, BusRepository busRepository) {
        this.dispatchRepository = dispatchRepository;
        this.driverRepository = driverRepository;
        this.busRepository = busRepository;
    }

    // DTO를 엔티티로 변환
    private Dispatch toEntity(DispatchDto dto) {
        Dispatch dispatch = new Dispatch();
        if (dto.getDispatchId() != null) {
            dispatch.setDispatchId(dto.getDispatchId());
        }
        // Driver 연결
        if (dto.getDriverId() != null) {
            Driver driver = driverRepository.findById(dto.getDriverId())
                    .orElseThrow(() -> new ResourceNotFoundException("Driver", "id", dto.getDriverId()));
            dispatch.setDriver(driver);
        }
        // Bus 연결
        if (dto.getBusId() != null) {
            Bus bus = busRepository.findById(dto.getBusId())
                    .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", dto.getBusId()));
            dispatch.setBus(bus);
        }
        dispatch.setStatus(dto.getStatus());
        dispatch.setDispatchDate(dto.getDispatchDate());
        dispatch.setScheduledDeparture(dto.getScheduledDeparture());
        dispatch.setActualDeparture(dto.getActualDeparture());
        dispatch.setActualArrival(dto.getActualArrival());
        dispatch.setWarningCount(dto.getWarningCount());
        dispatch.setDrivingScore(dto.getDrivingScore());
        return dispatch;
    }

    // 엔티티를 DTO로 변환
    private DispatchDto toDto(Dispatch entity) {
        return new DispatchDto(
                entity.getDispatchId(),
                entity.getDriver() != null ? entity.getDriver().getDriverId() : null,
                entity.getBus() != null ? entity.getBus().getBusId() : null,
                entity.getStatus(),
                entity.getDispatchDate(),
                entity.getScheduledDeparture(),
                entity.getActualDeparture(),
                entity.getActualArrival(),
                entity.getWarningCount(),
                entity.getDrivingScore(),
                entity.getDriver() != null ? entity.getDriver().getDriverName() : null,
                entity.getBus() != null ? entity.getBus().getVehicleNumber() : null
        );
    }

    // 모든 배차 조회
    @Transactional(readOnly = true)
    public List<DispatchDetailDto> getAllDispatches() {
        return dispatchRepository.findAll().stream()
                .map(DispatchDetailDto::from)
                .collect(Collectors.toList());
    }

    // ID로 특정 배차 조회
    @Transactional(readOnly = true)
    public DispatchDto getDispatchById(Long id) {
        Dispatch dispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", id));
        return toDto(dispatch);
    }

    // 새 배차 등록
    @Transactional
    public DispatchDto addDispatch(DispatchDto dispatchDto) {
        Dispatch dispatch = toEntity(dispatchDto);
        Dispatch savedDispatch = dispatchRepository.save(dispatch);
        return toDto(savedDispatch);
    }

    // 배차 정보 업데이트
    @Transactional
    public DispatchDto updateDispatch(Long id, DispatchDto dispatchDto) {
        Dispatch existingDispatch = dispatchRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "id", id));

        // DTO의 데이터로 엔티티 필드 업데이트
        Dispatch updatedDispatch = toEntity(dispatchDto);
        updatedDispatch.setDispatchId(existingDispatch.getDispatchId());

        // 필드별 업데이트
        existingDispatch.setStatus(dispatchDto.getStatus());
        existingDispatch.setDispatchDate(dispatchDto.getDispatchDate());
        existingDispatch.setScheduledDeparture(dispatchDto.getScheduledDeparture());
        existingDispatch.setActualDeparture(dispatchDto.getActualDeparture());
        existingDispatch.setActualArrival(dispatchDto.getActualArrival());
        existingDispatch.setWarningCount(dispatchDto.getWarningCount());
        existingDispatch.setDrivingScore(dispatchDto.getDrivingScore());

        Dispatch savedDispatch = dispatchRepository.save(existingDispatch);
        return toDto(savedDispatch);
    }

    // 배차 삭제
    @Transactional
    public void deleteDispatch(Long dispatchId) {
        dispatchRepository.findById(dispatchId)
                .orElseThrow(() -> new ResourceNotFoundException("Dispatch", "dispatchId", dispatchId));
        dispatchRepository.deleteById(dispatchId);
    }
}