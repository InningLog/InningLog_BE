package com.inninglog.inninglog.kbo.dto.gameSchdule;

import com.inninglog.inninglog.kbo.domain.Game;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Getter
@Builder
@AllArgsConstructor
public class GameSchResDto {

    @Schema(description = "경기 고유 ID", example = "20250701LGSS")
    private String gameId;

    @Schema(description = "경기 시작 날짜 및 시간", example = "2025-07-01T18:30:00")
    private LocalDateTime gameDate;

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

        return GameSchResDto.builder()
                .gameId(game.getGameId())
                .gameDate(game.getLocalDateTime())
                .supportTeamSC(supportTeamSC)
                .opponentSC(opponentSC)
                .stadiumSC(game.getStadium().getShortCode())
                .build();
    }

}