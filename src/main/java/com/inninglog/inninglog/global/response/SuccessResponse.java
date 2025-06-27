package com.inninglog.inninglog.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class SuccessResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    public static <T> SuccessResponse<T> success(SuccessCode successCode, T data) {
        return new SuccessResponse<>(successCode.getCode(), successCode.getMessage(), data);
    }

    public static SuccessResponse<Void> success(SuccessCode successCode) {
        return new SuccessResponse<>(successCode.getCode(), successCode.getMessage(), null);
    }
}