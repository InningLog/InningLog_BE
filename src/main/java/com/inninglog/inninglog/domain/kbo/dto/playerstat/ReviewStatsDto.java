package com.inninglog.inninglog.domain.kbo.dto.playerstat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

// 1. ReviewStatsDto
@Schema(description = "경기 선수 기록 전체 데이터")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class ReviewStatsDto {

    @Schema(description = "투수 기록 목록", example = "[{\"team\":\"한화\",\"playerName\":\"홍길동\",\"innings\":\"5.2\",\"earnedRuns\":3}]")
    private List<PitcherStatDto> pitchers = new ArrayList<>();

    @Schema(description = "타자 기록 목록", example = "[{\"team\":\"NC\",\"playerName\":\"김철수\",\"atBats\":4,\"hits\":2}]")
    private List<HitterStatDto> hitters = new ArrayList<>();
}