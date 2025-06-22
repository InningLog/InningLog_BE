package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

//개별 경기 결과 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameResultDto {
    private String awayTeam;
    private String homeTeam;
    private Integer awayScore;    // 실제 점수
    private Integer homeScore;    // 실제 점수
    private String stadium;
    private String gameDateTime;
    private String gameId;
    private String boxscoreUrl;   // 박스스코어 URL
    private String status;        // "COMPLETED"
}