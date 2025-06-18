package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PitcherStatDto {
    private String team;       // ex. "두산"
    private String playerName; // ex. "홍길동"
    private String innings;    // ex. "5.2"
    private int    earnedRuns; // ex. 3
}