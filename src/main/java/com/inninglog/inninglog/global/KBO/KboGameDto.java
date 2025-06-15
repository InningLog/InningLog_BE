package com.inninglog.inninglog.global.KBO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class KboGameDto {
    private String awayTeam;      // 원정팀
    private String homeTeam;      // 홈팀
    private String gameResult;    // 경기 결과 (예: "3:5", 혹은 "" 진행 중)
    private String stadium;       // 경기장
    private String gameDateTime;  // 경기 시작 시간 (예: "18:30")
}