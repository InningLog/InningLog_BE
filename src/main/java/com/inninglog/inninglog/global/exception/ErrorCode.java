package com.inninglog.inninglog.global.exception;

import lombok.Getter;

@Getter
public enum ErrorCode {

    USER_NOT_FOUND("USER_NOT_FOUND", "존재하지 않는 회원입니다."),
    INTERNAL_SERVER_ERROR("INTERNAL_SERVER_ERROR", "서버에 문제가 발생했습니다."),
    VALIDATION_ERROR("VALIDATION_ERROR", "요청값이 올바르지 않습니다."),
    METHOD_NOT_ALLOWED("METHOD_NOT_ALLOWED", "지원하지 않는 HTTP 메서드입니다."),
    TYPE_MISMATCH("TYPE_MISMATCH", "요청 파라미터 타입이 올바르지 않습니다.");

    private final String code;
    private final String message;

    ErrorCode(String code, String message) {
        this.code = code;
        this.message = message;
    }
}