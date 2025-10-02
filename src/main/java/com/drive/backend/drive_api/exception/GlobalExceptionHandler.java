package com.drive.backend.drive_api.exception;

import com.drive.backend.drive_api.common.ApiResponse; // ApiResponse import
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 - 리소스 없음
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleResourceNotFoundException(ResourceNotFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), null), HttpStatus.NOT_FOUND);
    }

    // 400 - 잘못된 요청 인자
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse<Void>> handleIllegalArgumentException(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return new ResponseEntity<>(ApiResponse.error(ex.getMessage(), null), HttpStatus.BAD_REQUEST);
    }

    // 400 처리 - @Valid 처리
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Map<String, String>>> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });

        log.warn("Validation failed: {}", errors);
        ApiResponse<Map<String, String>> response = ApiResponse.error("입력값이 유효하지 않습니다.", errors);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }

    // '사용자를 찾을 수 없음' 예외 처리 (404 Not Found)
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Void>> handleUsernameNotFoundException(UsernameNotFoundException ex) {
        log.warn("Login failed: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error("존재하지 않는 이메일입니다.", null);
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    // '비밀번호 불일치' 예외 처리 (401 Unauthorized)
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Void>> handleBadCredentialsException(BadCredentialsException ex) {
        log.warn("Login failed: Invalid credentials");
        ApiResponse<Void> response = ApiResponse.error("이메일 또는 비밀번호가 일치하지 않습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

    // 403 Forbidden - 권한 없음 예외 처리
    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Void>> handleAccessDeniedException(AccessDeniedException ex) {
        log.warn("Access Denied: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error("이 리소스에 접근할 권한이 없습니다.", null);
        return new ResponseEntity<>(response, HttpStatus.FORBIDDEN);
    }

    // 동시성 문제 (낙관적 락) 예외 처리 - 409 Conflict
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ResponseEntity<ApiResponse<Void>> handleOptimisticLockingFailureException(OptimisticLockingFailureException ex) {
        log.warn("Optimistic locking failure: {}", ex.getMessage());
        ApiResponse<Void> response = ApiResponse.error("다른 사용자에 의해 정보가 먼저 수정되었습니다. 페이지를 새로고침한 후 다시 시도해주세요.", null);
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    // 비즈니스 관련 예외를 처리 - 커스텀
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Void>> handleBusinessException(BusinessException ex) {
        ErrorCode errorCode = ex.getErrorCode();
        log.warn("Business exception occurred: {}", errorCode.getMessage());

        ApiResponse<Void> response = ApiResponse.error(errorCode.getMessage(), null);

        // ErrorCode에 정의된 HttpStatus와 메시지를 사용하여 응답 생성
        return new ResponseEntity<>(response, errorCode.getStatus());
    }

    // 500 처리
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleAllExceptions(Exception ex) {
        log.error("Unhandled exception occurred", ex);

        ApiResponse<Void> response = ApiResponse.error("서버 내부 오류가 발생했습니다. 관리자에게 문의하세요.", null);
        return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}