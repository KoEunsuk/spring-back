package com.drive.backend.drive_api.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
@Getter
@RequiredArgsConstructor
public enum ErrorCode {

    // 409 CONFLICT: 상태 충돌 또는 비즈니스 규칙 위반
    BUS_HAS_DISPATCHES(HttpStatus.CONFLICT, "해당 버스에 배차 기록이 존재하여 삭제할 수 없습니다."),
    DRIVER_HAS_DISPATCHES(HttpStatus.CONFLICT, "해당 운전자에 배차 기록이 존재하여 삭제할 수 없습니다."),
    DISPATCH_STATUS_NOT_SCHEDULED(HttpStatus.CONFLICT, "'예정' 상태인 배차만 작업을 시작할 수 있습니다."),
    DISPATCH_ALREADY_STARTED(HttpStatus.CONFLICT, "이미 시작된 배차는 변경 또는 취소할 수 없습니다."),
    BUS_ALREADY_DISPATCHED(HttpStatus.CONFLICT, "해당 버스는 요청된 시간에 이미 다른 배차가 있습니다."),
    DRIVER_ALREADY_DISPATCHED(HttpStatus.CONFLICT, "해당 운전자는 요청된 시간에 이미 다른 배차가 있습니다."),
    DISPATCH_NOT_IN_RUNNING_STATE(HttpStatus.CONFLICT, "'운행 중' 상태인 배차만 운행을 종료할 수 있습니다."),

    // 500 INTERNAL_SERVER_ERROR: 데이터 정합성 오류 등 심각한 서버 내부 문제
    DRIVER_WITHOUT_OPERATOR(HttpStatus.INTERNAL_SERVER_ERROR, "운전자의 소속 정보를 찾을 수 없습니다. 데이터 오류일 수 있으니 관리자에게 문의하세요.");

    private final HttpStatus status;
    private final String message;
}