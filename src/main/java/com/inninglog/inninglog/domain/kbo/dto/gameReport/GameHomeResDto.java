package com.inninglog.inninglog.domain.kbo.dto.gameReport;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameHomeResDto {

    @Schema(description = "내 응원 팀의 숏코드", example = "LG")
    private String myTeam;

    @Schema(description = "경기 상대 팀의 숏코드", example = "SSG")
    private String opponentTeam;

    @Schema(description = "경기장이 열리는 구장 숏코드", example = "JAM")
    private String stadium;

    @Schema(description = "경기 일시 (yyyy-MM-dd HH:mm)", example = "2025-07-10 18:30")
    private String gameDateTime;

    public static GameHomeResDto from(String myTeam, String opponentTeam, String stadium, String gameDateTime) {
        return GameHomeResDto.builder()
                .myTeam(myTeam)
                .opponentTeam(opponentTeam)
                .stadium(stadium)
                .gameDateTime(gameDateTime)
                .build();
    }
}