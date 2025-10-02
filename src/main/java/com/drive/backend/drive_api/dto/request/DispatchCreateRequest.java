package com.drive.backend.drive_api.dto.request;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class DispatchCreateRequest {

    @NotNull(message = "버스 ID는 필수입니다.")
    private Long busId;

    @NotNull(message = "운전자 ID는 필수입니다.")
    private Long driverId;

    @NotNull(message = "출발 예정 시간은 필수입니다.")
    @Future(message = "출발 예정 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime scheduledDepartureTime;

    @NotNull(message = "도착 예정 시간은 필수입니다.")
    @Future(message = "도착 예정 시간은 현재 시간 이후여야 합니다.")
    private LocalDateTime scheduledArrivalTime;
}
