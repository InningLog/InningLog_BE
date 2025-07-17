package com.inninglog.inninglog.home.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.home.dto.HomeResDto;
import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.gameReport.GameHomeResDto;
import com.inninglog.inninglog.kbo.dto.gameReport.WinningRateResult;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.kbo.service.GameReportService;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    private final MemberRepository memberRepository;
    private final GameRepository gameRepository;
    private final GameReportService gameReportService;

    @Transactional(readOnly = true)
    public HomeResDto homeView(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.error("❌ 유저를 찾을 수 없습니다. memberId: {}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        log.info("✅ 유저 조회 성공: memberId={}", memberId);

        WinningRateResult winningRateResult = gameReportService.forHomeCaculateWin(member);

        if (member.getTeam() == null) {
            log.error("❌ 유저의 응원팀이 설정되지 않음: memberId={}", memberId);
            throw new CustomException(ErrorCode.TEAM_NOT_FOUND);
        }

        List<GameHomeResDto> myTeamSchedule = getThisMonthGamesForTeam(member.getTeam().getId());

        return HomeResDto.from(winningRateResult.getWinningRateHalPoongRi(), myTeamSchedule);
    }

    // 유저의 응원팀 이번달 경기 조회
    public List<GameHomeResDto> getThisMonthGamesForTeam(Long teamId) {
        LocalDate today = LocalDate.now();
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        List<Game> games = gameRepository.findByTeamAndDateRange(teamId, startOfMonth, endOfMonth);

        if (games.isEmpty()) {
            log.warn("⚠️ 이번 달 팀 경기 일정이 없습니다. teamId: {}", teamId);
        } else {
            log.info("📅 {}월 경기 {}건 조회됨. teamId={}", today.getMonthValue(), games.size(), teamId);
        }

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