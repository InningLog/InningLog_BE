package com.inninglog.inninglog.domain.kbo.dto.gameSchdule;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// 개별 경기 일정 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameScheduleDto {
    private String awayTeam;
    private String homeTeam;
    private Integer awayScore;    // 일정 단계에서는 0
    private Integer homeScore;    // 일정 단계에서는 0
    private String stadium;
    private String gameDateTime;
    private String gameId;
    private String boxscoreUrl;   // 일정 단계에서는 null
    private String status;        // "SCHEDULED"
}