package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// 월별 경기 통계 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyGameStatsDto {
    private String yearMonth;
    private int totalGames;           // 전체 경기 수
    private int scheduledGames;       // 예정된 경기 수
    private int completedGames;       // 완료된 경기 수
    private int gamesWithBoxscore;    // 박스스코어 URL 있는 경기 수
    private int gamesWithPlayerStats; // 선수 기록 있는 경기 수
    private String lastUpdated;       // 마지막 업데이트 시간
}