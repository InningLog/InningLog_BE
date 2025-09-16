package com.inninglog.inninglog.kbo.dto.playerstat;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Schema(description = "타자 기록 데이터")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class HitterStatDto {

    @Schema(description = "소속 팀명", example = "NC", required = true)
    private String team;

    @Schema(description = "선수명", example = "김철수", required = true)
    private String playerName;

    @Schema(description = "타수", example = "4", required = true)
    private int atBats;

    @Schema(description = "안타", example = "2", required = true)
    private int hits;
}