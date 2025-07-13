package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.journal.repository.JournalRepository;
import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.domain.PlayerStat;
import com.inninglog.inninglog.kbo.domain.PlayerType;
import com.inninglog.inninglog.kbo.domain.VisitedGame;
import com.inninglog.inninglog.kbo.dto.PlayerRankingDto;
import com.inninglog.inninglog.kbo.dto.gameReport.GameReportResDto;
import com.inninglog.inninglog.kbo.dto.gameReport.PlayerRankingResult;
import com.inninglog.inninglog.kbo.dto.gameReport.WinningRateResult;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.kbo.repository.PlayerStatRepository;
import com.inninglog.inninglog.kbo.repository.VisitedGameRepository;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
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

    //나의 직관 게임 일정 기록
    public void createVisitedGame(Long memberId, String gameId, Long journalId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNAL_NOT_FOUND));

        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

        VisitedGame visitedGame = VisitedGame.builder()
                .member(member)
                .game(game)
                .resultScore(journal.getResultScore())
                .build();

        visitedGameRepository.save(visitedGame);
    }

    //나의 직관 승률 계산
    public WinningRateResult caculateWin(Member member) {
        List<VisitedGame> visitedGames = visitedGameRepository.findByMember(member);

        if (visitedGames.isEmpty()) {
            throw new CustomException(ErrorCode.NO_VISITED_GAMES);
        }

        int totalVisitedGames = visitedGames.size();
        int winGames = 0;
        int lossGames = 0;
        int drawGames = 0;

        for (VisitedGame visitedGame : visitedGames) {
            if (visitedGame.getResultScore().equals(ResultScore.WIN)) {
                winGames++;
            }
            else if(visitedGame.getResultScore().equals(ResultScore.LOSE)) {
                lossGames++;
            }
            else if(visitedGame.getResultScore().equals(ResultScore.DRAW)) {
                drawGames++;
            }
        }

        int winningRateHalPoongRi = (int) Math.round(((double) winGames / totalVisitedGames) * 1000);

        return new WinningRateResult(totalVisitedGames, winGames, lossGames, drawGames, winningRateHalPoongRi);
    }

    //홈 화면 용 나의 직관 승률 계산기
    public WinningRateResult forHomeCaculateWin(Member member) {
        List<VisitedGame> visitedGames = visitedGameRepository.findByMember(member);

        if (visitedGames.isEmpty()) {
            return WinningRateResult.empty();
        }

        int totalVisitedGames = visitedGames.size();
        int winGames = 0;
        int lossGames = 0;
        int drawGames = 0;

        for (VisitedGame visitedGame : visitedGames) {
            if (visitedGame.getResultScore().equals(ResultScore.WIN)) {
                winGames++;
            }
            else if(visitedGame.getResultScore().equals(ResultScore.LOSE)) {
                lossGames++;
            }
            else if(visitedGame.getResultScore().equals(ResultScore.DRAW)) {
                drawGames++;
            }
        }

        int winningRateHalPoongRi = (int) Math.round(((double) winGames / totalVisitedGames) * 1000);

        return new WinningRateResult(totalVisitedGames, winGames, lossGames, drawGames, winningRateHalPoongRi);
    }

    //선수들 경기 기록 계산
    public PlayerRankingResult calculatePlayer(Member member) {
        Team supportTeam = member.getTeam();

        if (supportTeam == null) {
            throw new CustomException(ErrorCode.TEAM_NOT_FOUND);
        }

        List<VisitedGame> visitedGames = visitedGameRepository.findByMember(member);

        Set<Long> gameIds = visitedGames.stream()
                .map(vg -> vg.getGame().getId())
                .collect(Collectors.toSet());

        if (gameIds.isEmpty()) {
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

        return new PlayerRankingResult(topBatters, topPitchers, bottomBatters, bottomPitchers);
    }

    //직관 리포트 생성
    public GameReportResDto generateReport(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        WinningRateResult winningRateResult = caculateWin(member);
        PlayerRankingResult rankingResult = calculatePlayer(member);

        Team team = teamRepository.findByShortCode(member.getTeam().getShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return GameReportResDto.from(winningRateResult, team.getWinRate(), rankingResult);
    }
}