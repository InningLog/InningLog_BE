package com.inninglog.inninglog.kbo.dto.gameSchdule;

import com.inninglog.inninglog.kbo.domain.Game;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

//직관일지에서 쓰는거
@Getter
@Builder
@AllArgsConstructor
public class GameSchResDto {

    private String gameId;
    private LocalDateTime gameDate;
    private String opponentSC;
    private String stadiumSC;

    public static GameSchResDto from(Game game, Long supportTeamId) {
        String opponentSC;

        if (!Objects.equals(game.getAwayTeam().getId(), supportTeamId)) {
            opponentSC = game.getAwayTeam().getShortCode();
        } else {
            opponentSC = game.getHomeTeam().getShortCode();
        }

        return GameSchResDto.builder()
                .gameId(game.getGameId())
                .gameDate(game.getLocalDateTime())
                .opponentSC(opponentSC)
                .stadiumSC(game.getStadium().getShortCode())
                .build();
    }

    public static List<GameSchResDto> listFrom(List<Game> games, Long supportTeamId) {
        return games.stream()
                .map(game -> from(game, supportTeamId))
                .collect(Collectors.toList());
    }
}