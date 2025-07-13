package com.inninglog.inninglog.kbo.dto.gameReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 유저의 직관 승률 요약 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WinningRateResult {

    private int totalVisitedGames;
    private int winGames;
    private int loseGames;
    private int drawGames;
    private int winningRateHalPoongRi;

    /**
     * 직관 경기 기록이 없을 경우를 위한 빈 결과
     */
    public static WinningRateResult empty() {
        return WinningRateResult.builder()
                .totalVisitedGames(0)
                .winGames(0)
                .loseGames(0)
                .drawGames(0)
                .winningRateHalPoongRi(0)
                .build();
    }
}