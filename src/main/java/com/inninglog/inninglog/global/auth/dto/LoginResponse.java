package com.inninglog.inninglog.global.auth.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponse {
    private Long memberId;
    private String token;

    public static LoginResponse create(Long memberId, String token) {
        return new LoginResponse(memberId, token);
    }
}