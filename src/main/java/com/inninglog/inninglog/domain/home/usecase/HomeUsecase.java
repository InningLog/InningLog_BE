package com.inninglog.inninglog.domain.home.usecase;

import com.inninglog.inninglog.domain.home.dto.HomeResDTO;
import com.inninglog.inninglog.domain.home.service.HomeService;
import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.GameHomeResDto;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.WinningRateResult;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.kbo.service.GameValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeUsecase {

    private final MemberValidateService memberValidateService;
    private final GameReportService gameReportService;
    private final GameValidateService gameValidateService;
    private final HomeService homeService;

    @Transactional(readOnly = true)
    public HomeResDTO homeView(Long memberId){
        Member member = memberValidateService.findById(memberId);
        memberValidateService.validateTeam(member);
        WinningRateResult winningRateResult = gameReportService.forHomeCaculateWin(member);
        List<Game> games = gameValidateService.findByTeam(member.getTeam().getId());
        List<GameHomeResDto> myTeamSchedule = homeService.getAllGamesForTeam(games, member.getTeam().getId());

        return HomeResDTO.from(member,winningRateResult.getWinningRateHalPoongRi(), myTeamSchedule);
    }
}
