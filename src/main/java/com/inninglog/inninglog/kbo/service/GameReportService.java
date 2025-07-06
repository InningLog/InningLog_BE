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

        ResultScore score = journal.getResultScore();
        if (score == null) {
            System.out.println("없음");
        }

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

        //할푼리로 계산
        int winningRateHalPoongRi = totalVisitedGames == 0
                ? 0 //경기 수가 0이면 0 반환
                : (int) Math.round(((double) winGames / totalVisitedGames) * 1000);

        return new WinningRateResult(totalVisitedGames, winGames, lossGames, drawGames, winningRateHalPoongRi);
    }


    //선수들 경기 기록 계산
    public PlayerRankingResult calculatePlayer(Member member) {

        Team supportTeam = member.getTeam();

        // 1. 유저가 직관한 경기들
        List<VisitedGame> visitedGames = visitedGameRepository.findByMember(member);

        //유저가 직관한 경기들의 ID만 추출해서 Set에 모음
        Set<Long> gameIds = visitedGames.stream() //스트림으로 변환
                .map(vg -> vg.getGame().getId()) //각 vistiedgame에서 game 객체를 꺼낸 뒤, 그 id만 추출
                .collect(Collectors.toSet()); //추출한 id를 set에 담음 -> 중복 제거용

        if (gameIds.isEmpty()) {
            //직관한 경기가 없는 경우 에러 던지기
            throw new CustomException(ErrorCode.GAME_NOT_FOUND);
            //return new PlayerRankingResult(Collections.emptyList(), Collections.emptyList());
        }

        // 2. 해당 경기들에서 응원팀 소속 선수의 기록만 조회
        List<PlayerStat> stats = playerStatRepository.findByGameIdsAndTeam(gameIds, supportTeam);

        // 3. 선수별 누적 스탯을 Map에 집계
        Map<Long, PlayerRankingDto> playerStatMap = new HashMap<>();

        for (PlayerStat stat : stats) {
            Long playerId = stat.getPlayer().getId();

            PlayerRankingDto dto =
                    playerStatMap.computeIfAbsent
                            (playerId, id ->
                    PlayerRankingDto
                            .from(stat.getPlayer()));//player 엔티티로부터 dto 초기화하는 팩토리 메서드

            dto.addStat(stat);
        }

        // 4. 할푼리 계산
        for (PlayerRankingDto dto : playerStatMap.values()) {
            dto.calculateHalPoongRi();
        }

        // 5. 투수/타자 분리 및 정렬
        List<PlayerRankingDto> topBatters = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.HITTER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi).reversed()) // 타자는 높은 순
                .limit(1)
                .toList();

        List<PlayerRankingDto> topPitchers = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.PITCHER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi))
                .limit(1)
                .toList();


        List<PlayerRankingDto> buttomBatters = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.HITTER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi))
                .limit(1)
                .toList();

        List<PlayerRankingDto> buttomPitchers = playerStatMap.values().stream()
                .filter(dto -> dto.getPlayerType() == PlayerType.PITCHER)
                .sorted(Comparator.comparingInt(PlayerRankingDto::getHalPoongRi).reversed())
                .limit(1)
                .toList();

        return new PlayerRankingResult(topBatters, topPitchers,buttomBatters,buttomPitchers);
    }

    // 결과 DTO
    public record PlayerRankingResult(
            List<PlayerRankingDto> topBatters,
            List<PlayerRankingDto> topPitchers,
            List<PlayerRankingDto> bottomBatters,
            List<PlayerRankingDto> bottomPitchers

    ) {}

    public record WinningRateResult(
            int totalVisitedGames,
            int winGames,
            int loseGames,
            int drawGames,
            int winningRateHalPoongRi
    ){}


    //직관 리포트 생성
    public GameReportResDto generateReport(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

       //직관 승률 계산
        WinningRateResult winningRateResult = caculateWin(member);

        // 선수 랭킹 계산
        PlayerRankingResult rankingResult = calculatePlayer(member);

        // 유저의 응원팀의 승률
        Team team = teamRepository.findByShortCode(member.getTeam().getShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));


        return GameReportResDto.builder()
                .totalVisitedGames(winningRateResult.totalVisitedGames)
                .winGames(winningRateResult.winGames)
                .loseGames(winningRateResult.loseGames)
                .drawGames(winningRateResult.drawGames)
                .myWeaningRate(winningRateResult.winningRateHalPoongRi)
                .teamWinRate(team.getWinRate())
                .topBatters(rankingResult.topBatters())
                .topPitchers(rankingResult.topPitchers())
                .bottomBatters(rankingResult.bottomBatters())
                .bottomPitchers(rankingResult.bottomPitchers())
                .build();
    }
}

