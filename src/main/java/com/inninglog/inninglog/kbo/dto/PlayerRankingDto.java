package com.inninglog.inninglog.kbo.dto;

import com.inninglog.inninglog.kbo.domain.Player;
import com.inninglog.inninglog.kbo.domain.PlayerStat;
import com.inninglog.inninglog.kbo.domain.PlayerType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PlayerRankingDto {

    private Long playerId;
    private String playerName;
    private PlayerType playerType;

    // 누적 스탯
    private int totalHits;
    private int totalAtBats;
    private int totalEarned;
    private double totalInning;

    // 할푼리
    private int halPoongRi;

    /**
     * Player → DTO 변환
     */
    public static PlayerRankingDto from(Player player) {
        return PlayerRankingDto.builder()
                .playerId(player.getId())
                .playerName(player.getName())
                .playerType(player.getPlayerType())
                .totalHits(0)
                .totalAtBats(0)
                .totalEarned(0)
                .totalInning(0.0)
                .halPoongRi(0)
                .build();
    }

    /**
     * PlayerStat로부터 누적 기록 추가
     */
    public void addStat(PlayerStat stat) {
        if (stat.getPlayerType() == PlayerType.HITTER) {
            this.totalHits += stat.getHits();
            this.totalAtBats += stat.getAt_bats();
        } else {
            this.totalEarned += stat.getEarned();
            this.totalInning += stat.getInning();
        }
    }

    /**
     * 누적된 데이터를 바탕으로 할푼리 계산
     */
    public void calculateHalPoongRi() {
        if (playerType == PlayerType.HITTER) {
            this.halPoongRi = totalAtBats == 0 ? 0 :
                    (int) Math.round(((double) totalHits / totalAtBats) * 1000);
        } else {
            this.halPoongRi = totalInning == 0 ? 0 :
                    (int) Math.round(((double) totalEarned / totalInning) * 1000);
        }
    }
}