package com.drive.backend.drive_api.dto.request;

import com.drive.backend.drive_api.enums.FuelType;
import com.drive.backend.drive_api.enums.RouteType;
import com.drive.backend.drive_api.enums.VehicleType;
import jakarta.validation.constraints.*;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class BusCreateDto {
    // 선택적 필드
    @Size(min = 1, message = "노선 번호는 비워둘 수 없습니다.")
    private String routeNumber;

    private RouteType routeType;

    @PastOrPresent(message = "마지막 정비일은 현재 또는 과거 날짜여야 합니다.")
    private LocalDate lastMaintenance;

    @PositiveOrZero(message = "수리 횟수는 0 이상의 숫자여야 합니다.")
    private Integer repairCount;

    // 필수 필드
    @NotNull(message = "탑승 정원은 필수입니다.")
    @Positive(message = "탑승 정원은 0보다 커야 합니다.")
    private Integer capacity;

    @NotBlank(message = "차량 번호는 필수입니다.")
    private String vehicleNumber;

    @NotNull(message = "차종은 필수입니다.")
    private VehicleType vehicleType;

    @NotNull(message = "차량 연식은 필수입니다.")
    private Integer vehicleYear;

    @NotNull(message = "연료 종류는 필수입니다.")
    private FuelType fuelType;
}
