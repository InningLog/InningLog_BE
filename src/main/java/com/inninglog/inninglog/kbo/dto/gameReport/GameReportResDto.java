package com.inninglog.inninglog.kbo.dto.gameReport;

import com.inninglog.inninglog.kbo.dto.PlayerRankingDto;
import com.inninglog.inninglog.kbo.service.GameReportService;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Schema(description = "직관 리포트 응답 DTO")
public class GameReportResDto {

    @Schema(description = "유저닉네임", example = "구혜승")
    private String nickname;

    @Schema(description = "직관한 총 경기 수", example = "2")
    private int totalVisitedGames;

    @Schema(description = "직관 중 응원팀이 승리한 횟수", example = "2")
    private int winGames;

    @Schema(description = "직관 중 응원팀이 패배한 횟수", example = "0")
    private int loseGames;

    @Schema(description = "직관 중 응원팀이 무승부한 횟수", example = "0")
    private int drawGames;

    @Schema(description = "직관 승률 (할푼리 기준, *1000)", example = "1000")
    private int myWeaningRate;

    @Schema(description = "응원팀의 실제 시즌 승률", example = "0.415")
    private double teamWinRate;

    @Schema(description = "직관 시 가장 성적이 좋았던 타자 1명", implementation = PlayerRankingDto.class)
    private List<PlayerRankingDto> topBatters;

    @Schema(description = "직관 시 가장 성적이 좋았던 투수 1명", implementation = PlayerRankingDto.class)
    private List<PlayerRankingDto> topPitchers;

    @Schema(description = "직관 시 가장 성적이 부진했던 타자 1명", implementation = PlayerRankingDto.class)
    private List<PlayerRankingDto> bottomBatters;

    @Schema(description = "직관 시 가장 성적이 부진했던 투수 1명", implementation = PlayerRankingDto.class)
    private List<PlayerRankingDto> bottomPitchers;


    public static GameReportResDto from(String nickname, WinningRateResult winningRateResult, double teamWinRate, PlayerRankingResult rankingResult) {
        return GameReportResDto.builder()
                .nickname(nickname)
                .totalVisitedGames(winningRateResult.getTotalVisitedGames())
                .winGames(winningRateResult.getWinGames())
                .loseGames(winningRateResult.getLoseGames())
                .drawGames(winningRateResult.getDrawGames())
                .myWeaningRate(winningRateResult.getWinningRateHalPoongRi())
                .teamWinRate(teamWinRate)
                .topBatters(rankingResult.getTopBatters())
                .topPitchers(rankingResult.getTopPitchers())
                .bottomBatters(rankingResult.getBottomBatters())
                .bottomPitchers(rankingResult.getBottomPitchers())
                .build();
    }
}