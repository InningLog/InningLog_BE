// 4. PlayerStatsSaveResult DTO (이미 있다면 스킵)
package com.inninglog.inninglog.domain.kbo.dto.playerstat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "선수 기록 저장 결과")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PlayerStatsSaveResult {

    @Schema(description = "새로 등록된 선수 수", example = "2")
    private int newPlayersCount;

    @Schema(description = "저장된 투수 기록 수", example = "5")
    private int pitcherStatsCount;

    @Schema(description = "저장된 타자 기록 수", example = "18")
    private int hitterStatsCount;
}