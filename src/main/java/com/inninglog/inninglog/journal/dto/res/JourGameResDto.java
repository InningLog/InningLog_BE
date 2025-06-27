package com.inninglog.inninglog.journal.dto.res;

import com.inninglog.inninglog.kbo.domain.Game;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourGameResDto {

    //게임 Id
    @Schema(description = "게임 Id", example = "20250619OBSS0")
    private String gameId;

    //경기 날짜
    @Schema(description = "경기 날짜", example = "2025-06-25T18:30:00")
    private LocalDateTime gameDate;

    //우리팀 숏코드
    @Schema(description = "우리팀 숏코드", example = "OB")
    private String supportTeamSC;

    //상대팀 숏코드
    @Schema(description = "상대팀 숏코드", example = "SS")
    private String opponentTeamSC;

    //경기장 숏코드
    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumSC;

    public static JourGameResDto fromGame (String supportTeamSC, String opponentTeamSC, Game game){
        return JourGameResDto.builder()
                .gameId(game.getGameId())
                .gameDate(game.getLocalDateTime())
                .supportTeamSC(supportTeamSC)
                .opponentTeamSC(opponentTeamSC)
                .stadiumSC(game.getStadium().getShortCode())
                .build();
    }
}
