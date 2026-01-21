package com.inninglog.inninglog.domain.kbo.dto.playerstat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "투수 기록 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PitcherStatDto {

    @Schema(description = "소속 팀명", example = "한화", required = true)
    private String team;

    @Schema(description = "선수명", example = "홍길동", required = true)
    private String playerName;

    @Schema(description = "투구 이닝", example = "5.2", required = true)
    private String innings;

    @Schema(description = "자책점", example = "3", required = true)
    private int earnedRuns;
}
