package com.inninglog.inninglog.home.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.home.dto.HomeResDto;
import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.gameReport.GameHomeResDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.kbo.service.GameReportService;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameReportService gameReportService;

    public HomeResDto homeView(Long memberId){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        GameReportService.WinningRateResult winningRateResult = gameReportService.caculateWin(member);

        List<GameHomeResDto> myTeamSchedule = getThisMonthGamesForTeam(member.getTeam().getId());

        return HomeResDto.from(winningRateResult.winningRateHalPoongRi(), myTeamSchedule);
    }

    //유저의 응원팀 이번달 경기 조회
    public List<GameHomeResDto> getThisMonthGamesForTeam(Long teamId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<Game> games = gameRepository.findByTeamAndDateRange(teamId, startOfMonth, endOfMonth);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return games.stream()
                .map(g -> {
                    boolean isHomeTeam = g.getHomeTeam().getId().equals(teamId);

                    String myTeam = isHomeTeam ? g.getHomeTeam().getShortCode() : g.getAwayTeam().getShortCode();
                    String opponentTeam = isHomeTeam ? g.getAwayTeam().getShortCode() : g.getHomeTeam().getShortCode();
                    String formattedDateTime = g.getLocalDateTime().format(formatter);

                    return GameHomeResDto.from(
                            myTeam,
                            opponentTeam,
                            g.getStadium().getShortCode(),
                            formattedDateTime
                    );
                })
                .toList();
    }
}
