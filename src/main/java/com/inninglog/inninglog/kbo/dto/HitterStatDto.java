package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HitterStatDto {
    private String team;       // ex. "LG 트윈스"
    private String playerName; // ex. "김철수"
    private int    atBats;     // ex. 4
    private int    hits;       // ex. 2
}