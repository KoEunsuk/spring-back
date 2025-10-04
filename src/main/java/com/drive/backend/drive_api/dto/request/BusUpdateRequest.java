package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.RouteType;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import jakarta.validation.constraints.Size;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusUpdateRequest {
    @Size(min = 1, message = "노선 번호는 비워둘 수 없습니다.")
    private String routeNumber;

    private RouteType routeType;

    @PastOrPresent(message = "마지막 정비일은 현재 또는 과거 날짜여야 합니다.")
    private LocalDate lastMaintenance;

    @Positive(message = "탑승 정원은 0보다 커야 합니다.")
    private Integer capacity;

    @Size(min = 1, message = "차량 번호는 비워둘 수 없습니다.")
    private String vehicleNumber;

    @PositiveOrZero(message = "수리 횟수는 0 이상이어야 합니다.")
    private Integer repairCount;
}
