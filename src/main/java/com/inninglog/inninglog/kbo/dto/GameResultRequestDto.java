package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

//응답 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameResultRequestDto {
    private List<GameResultDto> games;
    private String gameDate;   // "2025-06-01"
    private String type;       // "RESULTS"
}
