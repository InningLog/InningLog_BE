package com.inninglog.inninglog.domain.kbo.dto;

import com.inninglog.inninglog.domain.kbo.domain.Player;
import com.inninglog.inninglog.domain.kbo.domain.PlayerStat;
import com.inninglog.inninglog.domain.kbo.domain.PlayerType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "직관 리포트용 선수 순위 DTO")
public class PlayerRankingDto {

    @Schema(description = "선수 ID", example = "5")
    private Long playerId;

    @Schema(description = "선수 이름", example = "홍건희")
    private String playerName;

    @Schema(description = "선수 유형 (타자 / 투수)", example = "PITCHER")
    private PlayerType playerType;

    @Schema(description = "누적 안타 수 (타자 전용)", example = "1")
    private int totalHits;

    @Schema(description = "누적 타수 (타자 전용)", example = "3")
    private int totalAtBats;

    @Schema(description = "누적 자책점 (투수 전용)", example = "2")
    private int totalEarned;

    @Schema(description = "누적 이닝 수 (투수 전용)", example = "7.2")
    private double totalInning;

    @Schema(description = "할푼리 (타율 또는 방어율 환산값)", example = "333")
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
            this.halPoongRi = totalInning == 0
                    ? (totalEarned > 0 ? 9999 : 0)
                    : (int) Math.round(((double) totalEarned / totalInning) * 1000);
        }
    }
}