package com.drive.backend.drive_api.service;


import com.drive.backend.drive_api.dto.*;
import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.BusRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class BusService {

    private final BusRepository busRepository;
    private final OperatorRepository operatorRepository;

    public BusService(BusRepository busRepository, OperatorRepository operatorRepository) {
        this.busRepository = busRepository;
        this.operatorRepository = operatorRepository;
    }

    private Bus toEntity(BusDto dto) {
        Bus bus = new Bus();
        if (dto.getBusId() != null) {
            bus.setBusId(dto.getBusId());
        }
        bus.setRouteNumber(dto.getRouteNumber());
        bus.setRouteType(dto.getRouteType());
        bus.setCapacity(dto.getCapacity());
        bus.setVehicleNumber(dto.getVehicleNumber());
        bus.setVehicleType(dto.getVehicleType());
        bus.setVehicleYear(dto.getVehicleYear());
        bus.setLastMaintenance(dto.getLastMaintenance());
        bus.setRepairCount(dto.getRepairCount());

        if (dto.getOperatorId() != null) {
            Operator operator = operatorRepository.findById(dto.getOperatorId())
                    .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", dto.getOperatorId()));
            bus.setOperator(operator);
        } else {
            throw new IllegalArgumentException("Operator ID는 필수입니다.");
        }
        return bus;
    }

    private BusDto toDto(Bus entity) {
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
        return busRepository.findAll().stream()
                .map(BusListDto::from)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public BusDto getBusById(Long busId) {
        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "busId", busId));
        return BusDto.from(bus);
    }

    @Transactional
    public BusDto addBus(BusDto busDto) {
        if (busRepository.findByVehicleNumber(busDto.getVehicleNumber()).isPresent()) {
            throw new IllegalArgumentException("이미 존재하는 차량 번호입니다.");
        }
        Bus bus = toEntity(busDto);
        Bus savedBus = busRepository.save(bus);
        return toDto(savedBus);
    }

    @Transactional
    public BusDto updateBus(Long busId, BusUpdateRequestDto updateDto) {
        Bus existingBus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "busId", busId));

        if (updateDto.getRouteNumber() != null) {
            existingBus.setRouteNumber(updateDto.getRouteNumber());
        }
        if (updateDto.getCapacity() != null) {
            existingBus.setCapacity(updateDto.getCapacity());
        }
        if (updateDto.getLastMaintenance() != null) {
            existingBus.setLastMaintenance(updateDto.getLastMaintenance());
        }
        if (updateDto.getRepairCount() != null) {
            existingBus.setRepairCount(updateDto.getRepairCount());
        }

        return BusDto.from(existingBus);
    }

    @Transactional
    public void deleteBus(Long busId) {
        busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "busId", busId));
        busRepository.deleteById(busId);
    }

    @Transactional(readOnly = true)
    public List<BusLocationDto> getAllBusesLocations() {
        return busRepository.findAll().stream()
                .map(BusLocationDto::from)
                .collect(Collectors.toList());
    }
}