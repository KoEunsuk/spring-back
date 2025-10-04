package com.drive.backend.drive_api.enums;

public enum NotificationType {
    DRIVING_WARNING,    // 운행 중 경고
    NEW_DISPATCH_ASSIGNED, // 신규 배차 할당
    DISPATCH_STARTED,   // 운행 시작
    DISPATCH_CANCELED,  // 배차 취소
    DISPATCH_ENDED  // 운행 종료
}
