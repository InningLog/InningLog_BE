package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KboGamesResponse {
    private boolean success;
    private String message;
    private int savedCount;
}