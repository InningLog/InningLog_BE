package com.inninglog.inninglog.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.List;

import static com.inninglog.inninglog.global.exception.ErrorCode.*;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 1. CustomException 핸들러 (가장 중요!)
    // BaseException 핸들러를 이것으로 대체합니다.
    @ExceptionHandler(CustomException.class)
    public ResponseEntity<ApiResponse<Void>> handleCustomException(CustomException e) {
        ErrorCode errorCode = e.getErrorCode();
        log.warn("CustomException occurred: Code={}, Message={}", errorCode.getCode(), errorCode.getMessage());
        return createErrorResponse(errorCode, null);
    }

    // 2. @Valid 유효성 검증 예외
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<List<ErrorDetail>>> handleMethodArgumentValidation(MethodArgumentNotValidException e) {
        ErrorCode errorCode = VALIDATION_ERROR; // ErrorCode에서 VALIDATION_ERROR 사용
        List<ErrorDetail> errors = e.getBindingResult().getFieldErrors().stream()
                .map(fe -> ErrorDetail.of(fe.getField(), fe.getDefaultMessage(), fe.getRejectedValue()))
                .toList();
        log.warn("MethodArgumentNotValidException occurred: {}", e.getMessage());
        return createErrorResponse(errorCode, errors);
    }

    // 3. 타입 불일치 (e.g., /users/abc -> abc를 Long으로 변환 불가)
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Void>> handleTypeMismatch(MethodArgumentTypeMismatchException e) {
        ErrorCode errorCode = TYPE_MISMATCH;
        log.warn("TypeMismatch occurred: {}", e.getMessage());
        return createErrorResponse(errorCode, null);
    }

    // 4. 기타 Spring 기본 예외들도 ErrorCode를 사용하도록 통일
    @ExceptionHandler({
            MissingServletRequestParameterException.class,
            HttpMessageNotReadableException.class,
            NoResourceFoundException.class
    })
    public ResponseEntity<ApiResponse<Void>> handleBadRequestExceptions(Exception e) {
        ErrorCode errorCode;
        if (e instanceof MissingServletRequestParameterException) {
            errorCode = PARAMETER_NOT_FOUND;
        } else if (e instanceof HttpMessageNotReadableException) {
            errorCode = VALIDATION_ERROR; // 혹은 별도 ErrorCode 생성
        } else {
            errorCode = RESOURCE_NOT_FOUND;
        }
        log.warn("BadRequest exception occurred: {}", e.getMessage());
        return createErrorResponse(errorCode, null);
    }

    // 5. 최후의 보루: 서버 내부 오류
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Void>> handleException(Exception e) {
        ErrorCode errorCode = INTERNAL_SERVER_ERROR;
        log.error("Unhandled exception occurred", e); // 스택 트레이스를 포함하여 로깅
        return createErrorResponse(errorCode, null);
    }

    // --- Helper Methods ---

    // 응답 생성 로직을 별도 메서드로 분리
    private <T> ResponseEntity<ApiResponse<T>> createErrorResponse(ErrorCode errorCode, T data) {
        return ResponseEntity
                .status(errorCode.getStatus())
                .body(ApiResponse.response(errorCode.getStatus(), errorCode.getMessage(), data));
    }
}