package com.drive.backend.drive_api.service;


import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.entity.Bus_old;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.BusRepository_old;
import com.drive.backend.drive_api.repository.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService_old {

    private final BusRepository_old busRepositoryOld;
    private final OperatorRepository operatorRepository;

    public BusService_old(BusRepository_old busRepositoryOld, OperatorRepository operatorRepository) {
        this.busRepositoryOld = busRepositoryOld;
        this.operatorRepository = operatorRepository;
    }

    private Bus_old toEntity(BusDto dto) {
        Bus_old busOld = new Bus_old();
        if (dto.getBusId() != null) {
            busOld.setBusId(dto.getBusId());
        }
        busOld.setRouteNumber(dto.getRouteNumber());
        busOld.setRouteType(dto.getRouteType());
        busOld.setCapacity(dto.getCapacity());
        busOld.setVehicleNumber(dto.getVehicleNumber());
        busOld.setVehicleType(dto.getVehicleType());
        busOld.setVehicleYear(dto.getVehicleYear());
        busOld.setLastMaintenance(dto.getLastMaintenance());
        busOld.setRepairCount(dto.getRepairCount());

        if (dto.getOperatorId() != null) {
            Operator operator = operatorRepository.findById(dto.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", dto.getOperatorId()));
            busOld.setOperator(operator);
        } else {
            throw new IllegalArgumentException("Operator ID는 필수입니다.");
        }
        return busOld;
    }

    private BusDto toDto(Bus_old entity) {
        return new BusDto(
//                entity.getBusId(),
//                entity.getRouteNumber(),
//                entity.getRouteType(),
//                entity.getCapacity(),
//                entity.getVehicleNumber(),
//                entity.getVehicleType(),
//                entity.getVehicleYear(),
//                entity.getLastMaintenance(),
//                entity.getRepairCount(),
//                entity.getOperator() != null ? entity.getOperator().getOperatorId() : null,
//                entity.getFuelType(),
//                entity.getTotalMileage(),
//                entity.getAverageFuelEfficiency()
        );
    }

    @Transactional(readOnly = true)
    public List<BusListDto> getAllBuses() {
        return busRepositoryOld.findAll().stream()
                .map(BusListDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusDto getBusById(Long busId) {
        Bus_old busOld = busRepositoryOld.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "busId", busId));
        return BusDto.from(busOld);
    }

    @Transactional
    public BusDto addBus(BusDto busDto) {
        if (busRepositoryOld.findByVehicleNumber(busDto.getVehicleNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 차량 번호입니다.");
        }
        Bus_old busOld = toEntity(busDto);
        Bus_old savedBusOld = busRepositoryOld.save(busOld);
        return toDto(savedBusOld);
    }

    @Transactional
    public BusDto updateBus(Long busId, BusUpdateRequestDto updateDto) {
        Bus_old existingBusOld = busRepositoryOld.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "busId", busId));

        if (updateDto.getRouteNumber() != null) {
            existingBusOld.setRouteNumber(updateDto.getRouteNumber());
        }
        if (updateDto.getCapacity() != null) {
            existingBusOld.setCapacity(updateDto.getCapacity());
        }
        if (updateDto.getLastMaintenance() != null) {
            existingBusOld.setLastMaintenance(updateDto.getLastMaintenance());
        }
        if (updateDto.getRepairCount() != null) {
            existingBusOld.setRepairCount(updateDto.getRepairCount());
        }

        return BusDto.from(existingBusOld);
    }

    @Transactional
    public void deleteBus(Long busId) {
        busRepositoryOld.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "busId", busId));
        busRepositoryOld.deleteById(busId);
    }

    @Transactional(readOnly = true)
    public List<BusLocationDto> getAllBusesLocations() {
        return busRepositoryOld.findAll().stream()
                .map(BusLocationDto::from)
                .collect(Collectors.toList());
    }
}