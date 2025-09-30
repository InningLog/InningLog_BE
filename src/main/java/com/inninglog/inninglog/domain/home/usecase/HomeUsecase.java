package com.inninglog.inninglog.domain.home.usecase;

import com.inninglog.inninglog.domain.home.dto.HomeResDTO;
import com.inninglog.inninglog.domain.home.service.HomeService;
import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.GameHomeResDto;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.WinningRateResult;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.kbo.service.GameVaildateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MembeValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class HomeUsecase {

    private final MembeValidateService membeValidateService;
    private final GameReportService gameReportService;
    private final GameVaildateService gameVaildateService;
    private final HomeService homeService;

    @Transactional(readOnly = true)
    public HomeResDTO homeView(Long memberId){
        Member member = membeValidateService.findById(memberId);
        WinningRateResult winningRateResult = gameReportService.forHomeCaculateWin(member);
        membeValidateService.validateTeam(member);
        List<Game> games = gameVaildateService.findByTeam(member.getTeam().getId());
        List<GameHomeResDto> myTeamSchedule = homeService.getAllGamesForTeam(games, member.getTeam().getId());

        return HomeResDTO.from(member,winningRateResult.getWinningRateHalPoongRi(), myTeamSchedule);
    }
}
