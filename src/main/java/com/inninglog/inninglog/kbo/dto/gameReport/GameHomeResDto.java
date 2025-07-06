package com.inninglog.inninglog.kbo.dto.gameReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GameHomeResDto {
    private String myTeam;
    private String opponentTeam;
    private String stadium;
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
