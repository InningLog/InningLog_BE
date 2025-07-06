package com.inninglog.inninglog.kbo.dto.gameReport;

import com.inninglog.inninglog.kbo.dto.PlayerRankingDto;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameReportResDto {
    private int totalVisitedGames;
    private int winGames;
    private int loseGames;
    private int drawGames;
    private int myWeaningRate;
    private double teamWinRate;

    private List<PlayerRankingDto> topBatters;
    private List<PlayerRankingDto> topPitchers;

    private List<PlayerRankingDto> bottomBatters;
    private List<PlayerRankingDto> bottomPitchers;
}


