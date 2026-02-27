package com.inninglog.inninglog.domain.kbo.dto.gameReport;

public interface WinningRateProjection {
    int getTotalGames();
    int getWinGames();
    int getLoseGames();
    int getDrawGames();
}
