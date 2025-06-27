package com.inninglog.inninglog.journal.dto;

import com.inninglog.inninglog.kbo.domain.Game;
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
    private String gameId;

    //경기 날짜
    private LocalDateTime gameDate;

    //우리팀 숏코드
    private String supportTeamSC;

    //상대팀 숏코드
    private String opponentTeamSC;

    //경기장 숏코드
    private String stadiumSC;

    public static JourGameResDto fromGame (String supportTeamSC, String opponentTeamSC, Game game){
        return JourGameResDto.builder()
                .gameId(game.getGameId())
                .gameDate(game.getLocalDateTime())
                .supportTeamSC(supportTeamSC)
                .opponentTeamSC(opponentTeamSC).build();
    }
}
