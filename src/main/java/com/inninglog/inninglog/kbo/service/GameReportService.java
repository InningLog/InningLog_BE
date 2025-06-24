package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.journal.repository.JournalRepository;
import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.domain.VisitedGame;
import com.inninglog.inninglog.kbo.dto.gameReport.GameReportResDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.kbo.repository.VisitedGameRepository;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameReportService {

    private final MemberRepository memberRepository;
    private final JournalRepository journalRepository;
    private final GameRepository gameRepository;
    private final VisitedGameRepository visitedGameRepository;

    //나의 직관 게임 일정 기록
    public void createVisitedGame(Long memberId, String gameId, Long journalId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNAL_NOT_FOUND));

        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));

        boolean isWin = journal.getResultScore() == ResultScore.WIN;

        VisitedGame visitedGame = VisitedGame.builder()
                .member(member)
                .game(game)
                .result(isWin)
                .build();

        visitedGameRepository.save(visitedGame);

    }

    //나의 직관 승률 계산
    public GameReportResDto caculateWin(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<VisitedGame> visitedGames = visitedGameRepository.findByMember(member);

        int totalVisitedGames = visitedGames.size();
        int winGames = 0;

        for (VisitedGame visitedGame : visitedGames) {
            if (Boolean.TRUE.equals(visitedGame.getResult())) {
                winGames++;
            }
        }

        //할푼리로 계산
        int winningRateHalPoongRi = totalVisitedGames == 0
                ? 0 //경기 수가 0이면 0 반환
                : (int) Math.round(((double) winGames / totalVisitedGames) * 1000);

        return GameReportResDto.builder()
                .win(winGames)
                .visited(totalVisitedGames)
                .winningRate(winningRateHalPoongRi)
                .build();
    }


}
