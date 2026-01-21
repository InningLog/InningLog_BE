package com.inninglog.inninglog.domain.kbo.dto.gameReport;

import com.inninglog.inninglog.domain.kbo.dto.PlayerRankingDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.Collections;
import java.util.List;

/**
 * 유저 직관 기준 선수별 퍼포먼스 랭킹 DTO
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRankingResult {

    private List<PlayerRankingDto> topBatters;
    private List<PlayerRankingDto> topPitchers;
    private List<PlayerRankingDto> bottomBatters;
    private List<PlayerRankingDto> bottomPitchers;

    /**
     * 직관 기록이 없는 경우를 위한 빈 결과
     */
    public static PlayerRankingResult empty() {
        return PlayerRankingResult.builder()
                .topBatters(Collections.emptyList())
                .topPitchers(Collections.emptyList())
                .bottomBatters(Collections.emptyList())
                .bottomPitchers(Collections.emptyList())
                .build();
    }
}