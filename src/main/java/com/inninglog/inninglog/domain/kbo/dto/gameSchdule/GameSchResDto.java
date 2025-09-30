package com.inninglog.inninglog.domain.kbo.dto.gameSchdule;

import com.inninglog.inninglog.domain.kbo.domain.Game;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

@Getter
@Builder
@AllArgsConstructor
public class GameSchResDto {

    @Schema(description = "경기 고유 ID", example = "20250701LGSS")
    private String gameId;

    @Schema(description = "경기 시작 날짜 및 시간", example = "2025-07-01 18:30")
    private String gameDate;

    @Schema(description = "우리팀 숏코드", example = "OB")
    private String supportTeamSC;

    @Schema(description = "상대 팀의 구단 식별자 (shortCode)", example = "SS")
    private String opponentSC;

    @Schema(description = "경기장이 위치한 구장의 식별자 (shortCode)", example = "JAMSIL")
    private String stadiumSC;

    public static GameSchResDto from(Game game, Long supportTeamId) {
        String opponentSC;
        String supportTeamSC;

        if (!Objects.equals(game.getAwayTeam().getId(), supportTeamId)) {
            opponentSC = game.getAwayTeam().getShortCode();
            supportTeamSC = game.getHomeTeam().getShortCode();
        } else {
            opponentSC = game.getHomeTeam().getShortCode();
            supportTeamSC = game.getAwayTeam().getShortCode();
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = game.getLocalDateTime().format(formatter);


        return GameSchResDto.builder()
                .gameId(game.getGameId())
                .gameDate(formattedDate)
                .supportTeamSC(supportTeamSC)
                .opponentSC(opponentSC)
                .stadiumSC(game.getStadium().getShortCode())
                .build();
    }

}