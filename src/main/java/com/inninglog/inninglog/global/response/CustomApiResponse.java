package com.inninglog.inninglog.global.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomApiResponse<T> {
    private final String code;
    private final String message;
    private final T data;

    public static <T> CustomApiResponse<T> success(SuccessCode successCode, T data) {
        return new CustomApiResponse<>(successCode.getCode(), successCode.getMessage(), data);
    }

    public static CustomApiResponse<Void> success(SuccessCode successCode) {
        return new CustomApiResponse<>(successCode.getCode(), successCode.getMessage(), null);
    }
}