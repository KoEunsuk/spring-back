package com.drive.backend.drive_api.service;

import com.drive.backend.drive_api.dto.request.BusCreateDto;
import com.drive.backend.drive_api.dto.request.BusUpdateRequestDto;
import com.drive.backend.drive_api.dto.response.BusDetailDto;
import com.drive.backend.drive_api.entity.Bus;
import com.drive.backend.drive_api.entity.Operator;
import com.drive.backend.drive_api.exception.ResourceNotFoundException;
import com.drive.backend.drive_api.repository.BusRepository;
import com.drive.backend.drive_api.repository.OperatorRepository;
import com.drive.backend.drive_api.security.SecurityUtil;
import com.drive.backend.drive_api.security.userdetails.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BusService {
    
    private final BusRepository busRepository;
    private final OperatorRepository operatorRepository;

    // 모든 버스 조회
    public List<BusDetailDto> getAllBuses() {
        Long operatorId = getCurrentOperator().getOperatorId();
        List<Bus> buses = busRepository.findAllByOperator_OperatorId(operatorId);
        return buses.stream().map(BusDetailDto::from).collect(Collectors.toList());
    }

    // 버스 추가(생성)
    @Transactional
    public BusDetailDto createBus(BusCreateDto createDto) {
        Operator currentOperator = getCurrentOperator();

        busRepository.findByVehicleNumber(createDto.getVehicleNumber()).ifPresent(bus -> {
            throw new IllegalArgumentException("이미 등록된 차량 번호입니다: " + createDto.getVehicleNumber());
        });

        Bus newBus = new Bus(
                createDto.getCapacity(),
                createDto.getVehicleNumber(),
                createDto.getVehicleType(),
                createDto.getVehicleYear(),
                currentOperator,
                createDto.getFuelType());

        if (createDto.getRouteNumber() != null && !createDto.getRouteNumber().isBlank()) {
            newBus.setRouteNumber(createDto.getRouteNumber());
        }
        if (createDto.getRouteType() != null) {
            newBus.setRouteType(createDto.getRouteType());
        }
        if (createDto.getLastMaintenance() != null) {
            newBus.setLastMaintenance(createDto.getLastMaintenance());
        }
        if (createDto.getRepairCount() != null) {
            newBus.setRepairCount(createDto.getRepairCount());
        }

        currentOperator.addBus(newBus);

        return BusDetailDto.from(busRepository.save(newBus));
    }

    // 버스 수정
    @Transactional
    public BusDetailDto updateBus(Long busId, BusUpdateRequestDto updateDto) {
        Bus bus = findBusAndCheckPermission(busId);

        if (updateDto.getVehicleNumber() != null && !updateDto.getVehicleNumber().isBlank()) {
            String newVehicleNumber = updateDto.getVehicleNumber();

            // 새로운 차량 번호를 가진 버스가 있는지 찾아봅니다.
            busRepository.findByVehicleNumber(newVehicleNumber)
                    // 찾은 버스가 현재 수정하려는 버스와 다른 버스일 경우에만 예외를 발생시킵니다.
                    .ifPresent(foundBus -> {
                        if (!foundBus.getBusId().equals(bus.getBusId())) {
                            throw new IllegalArgumentException("이미 다른 버스가 사용 중인 차량 번호입니다: " + newVehicleNumber);
                        }
                    });

            // 중복이 아니면 차량 번호를 업데이트합니다.
            bus.setVehicleNumber(newVehicleNumber);
        }

        if (updateDto.getRouteNumber() != null) bus.setRouteNumber(updateDto.getRouteNumber());
        if (updateDto.getRouteType() != null) bus.setRouteType(updateDto.getRouteType());
        if (updateDto.getLastMaintenance() != null) bus.setLastMaintenance(updateDto.getLastMaintenance());
        if (updateDto.getCapacity() != null) bus.setCapacity(updateDto.getCapacity());
        if (updateDto.getRepairCount() != null) bus.setRepairCount(updateDto.getRepairCount());

        return BusDetailDto.from(bus);
    }

    // 특정 버스 상세 조회
    public BusDetailDto findBusById(Long busId) {
        Bus bus = findBusAndCheckPermission(busId);
        return BusDetailDto.from(bus);
    }

    // 버스 삭제
    @Transactional
    public void deleteBus(Long busId) {
        Bus bus = findBusAndCheckPermission(busId);

        if (!bus.getDispatches().isEmpty()) {
            throw new IllegalStateException("해당 버스에 배차 기록이 존재하여 삭제할 수 없습니다.");
        }

        busRepository.delete(bus);
    }

    // 헬퍼 메서드: 현재 로그인한 사용자의 Operator 엔티티를 가져옴
    private Operator getCurrentOperator() {
        Long operatorId = SecurityUtil.getCurrentUser()
                .map(CustomUserDetails::getOperatorId)
                .orElseThrow(() -> new RuntimeException("인증 정보가 없습니다."));

        return operatorRepository.findById(operatorId)
                .orElseThrow(() -> new ResourceNotFoundException("Operator", "id", operatorId));
    }

    // 헬퍼 메서드: 버스를 관리하기 위한 사용자의 권한을 확인
    private Bus findBusAndCheckPermission(Long busId) {
        Long currentOperatorId = getCurrentOperator().getOperatorId();

        Bus bus = busRepository.findById(busId)
                .orElseThrow(() -> new ResourceNotFoundException("Bus", "id", busId));

        if (!currentOperatorId.equals(bus.getOperator().getOperatorId())) {
            throw new AccessDeniedException("다른 운수사의 버스를 관리할 권한이 없습니다.");
        }
        return bus;
    }

}
