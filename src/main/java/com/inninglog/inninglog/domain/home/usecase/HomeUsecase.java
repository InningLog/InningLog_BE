package com.inninglog.inninglog.domain.home.usecase;

import com.inninglog.inninglog.domain.home.dto.HomeResDTO;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.WinningRateResult;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MembeValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class HomeUsecase {

    private MembeValidateService membeValidateService;
    private GameReportService gameReportService;

    @Transactional(readOnly = true)
    public HomeResDTO homeView(Long memberId){
        Member member = membeValidateService.findById(memberId);
        WinningRateResult winningRateResult = gameReportService.forHomeCaculateWin(member);

    }
}
