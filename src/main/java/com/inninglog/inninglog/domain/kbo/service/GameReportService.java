package com.inninglog.inninglog.domain.kbo.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.repository.JournalRepository;
import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.domain.PlayerStat;
import com.inninglog.inninglog.domain.kbo.domain.PlayerType;
import com.inninglog.inninglog.domain.kbo.domain.VisitedGame;
import com.inninglog.inninglog.domain.kbo.dto.PlayerRankingDto;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.GameReportResDto;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.PlayerRankingResult;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.WinningRateProjection;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.WinningRateResult;
import com.inninglog.inninglog.domain.kbo.repository.GameRepository;
import com.inninglog.inninglog.domain.kbo.repository.PlayerStatRepository;
import com.inninglog.inninglog.domain.kbo.repository.VisitedGameRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameReportService {

    private final MemberRepository memberRepository;
    private final JournalRepository journalRepository;
    private final GameRepository gameRepository;
    private final VisitedGameRepository visitedGameRepository;
    private final PlayerStatRepository playerStatRepository;
    private final TeamRepository teamRepository;

    // 나의 직관 게임 일정 기록
    public void createVisitedGame(Long memberId, String gameId, Long journalId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.info("📌 [createVisitedGame] memberId={} 유저를 찾을 수 없습니다", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> {
                    log.info("📌 [createVisitedGame] journalId={} 일지를 찾을 수 없습니다", journalId);
                    return new CustomException(ErrorCode.JOURNAL_NOT_FOUND);
                });

        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> {
                    log.info("📌 [createVisitedGame] gameId='{}' 게임을 찾을 수 없습니다", gameId);
                    return new CustomException(ErrorCode.GAME_NOT_FOUND);
                });

        VisitedGame visitedGame = VisitedGame.builder()
                .member(member)
                .game(game)
                .resultScore(journal.getResultScore())
                .build();

        visitedGameRepository.save(visitedGame);
        log.info("📌 [createVisitedGame] memberId={}, gameId='{}', journalId={} 직관 게임 저장 완료: resultScore={}",
                memberId, gameId, journalId, journal.getResultScore());
    }

    // 나의 직관 승률 계산
    public WinningRateResult caculateWin(Member member) {
        WinningRateProjection projection = visitedGameRepository.countWinningRate(member)
                .orElseThrow(() -> {
                    log.info("📌 [caculateWin] memberId={} 직관 기록이 없습니다", member.getId());
                    return new CustomException(ErrorCode.NO_VISITED_GAMES);
                });

        if (projection.getTotalGames() == 0) {
            log.info("📌 [caculateWin] memberId={} 직관 기록이 없습니다", member.getId());
            throw new CustomException(ErrorCode.NO_VISITED_GAMES);
        }

        int totalVisitedGames = projection.getTotalGames();
        int winGames = projection.getWinGames();
        int lossGames = projection.getLoseGames();
        int drawGames = projection.getDrawGames();
        int winningRateHalPoongRi = (int) Math.round(((double) winGames / totalVisitedGames) * 1000);

        log.info("📌 [caculateWin] memberId={} 직관 승률 계산 완료: total={}, win={}, lose={}, draw={}, rate={}",
                member.getId(), totalVisitedGames, winGames, lossGames, drawGames, winningRateHalPoongRi);

        return new WinningRateResult(totalVisitedGames, winGames, lossGames, drawGames, winningRateHalPoongRi);
    }

    // 홈 화면 용 승률 계산
    public WinningRateResult forHomeCaculateWin(Member member) {
        WinningRateProjection projection = visitedGameRepository.countWinningRate(member)
                .orElse(null);

        if (projection == null || projection.getTotalGames() == 0) {
            log.info("📌 [forHomeCaculateWin] memberId={} 홈화면용 직관 기록 없음", member.getId());
            return WinningRateResult.empty();
        }

        int totalVisitedGames = projection.getTotalGames();
        int winGames = projection.getWinGames();
        int lossGames = projection.getLoseGames();
        int drawGames = projection.getDrawGames();
        int winningRateHalPoongRi = (int) Math.round(((double) winGames / totalVisitedGames) * 1000);

        log.info("📌 [forHomeCaculateWin] memberId={} 홈화면 승률 계산 완료: rate={}",
                member.getId(), winningRateHalPoongRi);

        return new WinningRateResult(totalVisitedGames, winGames, lossGames, drawGames, winningRateHalPoongRi);
    }

    // 선수들 기록 계산
    public PlayerRankingResult calculatePlayer(Member member) {
        Team supportTeam = member.getTeam();

        if (supportTeam == null) {
            log.info("📌 [calculatePlayer] memberId={} 응원팀이 설정되지 않았습니다", member.getId());
            throw new CustomException(ErrorCode.TEAM_NOT_FOUND);
        }

        List<VisitedGame> visitedGames = visitedGameRepository.findByMember(member);
        Set<Long> gameIds = visitedGames.stream().map(vg -> vg.getGame().getId()).collect(Collectors.toSet());

        if (gameIds.isEmpty()) {
            log.info("📌 [calculatePlayer] memberId={} 직관한 경기 없음", member.getId());
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
        }

        List<PlayerStat> stats = playerStatRepository.findByGameIdsAndTeam(gameIds, supportTeam);
        Map<Long, PlayerRankingDto> playerStatMap = new HashMap<>();

        for (PlayerStat stat : stats) {
            Long playerId = stat.getPlayer().getId();
            PlayerRankingDto dto = playerStatMap.computeIfAbsent(
                    playerId, id -> PlayerRankingDto.from(stat.getPlayer()));
            dto.addStat(stat);
        }

        for (PlayerRankingDto dto : playerStatMap.values()) {
            dto.calculateHalPoongRi();
        }

        List<PlayerRankingDto> topBatters = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.HITTER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi).reversed())
                .limit(1)
                .toList();

        List<PlayerRankingDto> topPitchers = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.PITCHER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi))
                .limit(1)
                .toList();

        List<PlayerRankingDto> bottomBatters = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.HITTER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi))
                .limit(1)
                .toList();

        List<PlayerRankingDto> bottomPitchers = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.PITCHER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi).reversed())
                .limit(1)
                .toList();

        log.info("📌 [calculatePlayer] memberId={} 선수 랭킹 계산 완료: 총 선수 수={}",
                member.getId(), playerStatMap.size());

        return new PlayerRankingResult(topBatters, topPitchers, bottomBatters, bottomPitchers);
    }

    // 직관 리포트 생성
    public GameReportResDto generateReport(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.info("📌 [generateReport] memberId={} 유저를 찾을 수 없습니다", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        WinningRateResult winningRateResult = caculateWin(member);
        PlayerRankingResult rankingResult = calculatePlayer(member);

        Team team = teamRepository.findByShortCode(member.getTeam().getShortCode())
                .orElseThrow(() -> {
                    log.info("📌 [generateReport] shortCode='{}' 응원팀 정보 불일치", member.getTeam().getShortCode());
                    return new CustomException(ErrorCode.TEAM_NOT_FOUND);
                });

        log.info("📌 [generateReport] memberId={}, team='{}' 직관 리포트 생성 완료",
                memberId, team.getShortCode());

        return GameReportResDto.from(member.getNickname(),winningRateResult, team.getWinRate(), rankingResult);
    }
}