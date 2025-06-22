package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


// 박스스코어 URL 있는 경기 DTO
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameWithBoxscoreDto {
    private String gameId;
    private String awayTeam;
    private String homeTeam;
    private String boxscoreUrl;
    private String gameDateTime;
    private String stadium;
}
