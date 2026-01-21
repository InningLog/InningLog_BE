package com.inninglog.inninglog.global.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class SignupRequest {
    @NotBlank
    private String userID;

    @NotBlank
    private String password;
}


