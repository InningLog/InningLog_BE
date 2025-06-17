package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KboGameDto {
    private String awayTeam;      // 원정팀
    private String homeTeam;      // 홈팀
    private int awayScore;
    private int homeScore;
    private String stadium;       // 경기장
    private String gameDateTime;  // 경기 시작 시간 (예: "18:30")
}