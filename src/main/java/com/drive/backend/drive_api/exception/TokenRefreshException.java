package com.drive.backend.drive_api.exception;

import lombok.Getter;

@Getter
public class TokenRefreshException extends RuntimeException {

    private final String token;

    public TokenRefreshException(String token, String message) {
        super(message);
        this.token = token;
    }
}