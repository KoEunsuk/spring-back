package com.drive.backend.drive_api.entity;

import lombok.Getter;

@Getter
public enum Status {
    REST("휴식"),
    WAITING("대기"),
    DRIVING("운행중");

    private final String value;

    Status(String value) {
        this.value = value;
    }
}
