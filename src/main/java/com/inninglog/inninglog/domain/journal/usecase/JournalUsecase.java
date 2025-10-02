package com.inninglog.inninglog.domain.journal.usecase;

import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.res.JourCreateResDto;
import com.inninglog.inninglog.domain.journal.service.JournalService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.service.TeamvalidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JournalUsecase {

    private final JournalService journalService;
    private final MemberValidateService memberValidateService;
    private final TeamvalidateService teamvalidateService;

    @Transactional
    public JourCreateResDto createJournal(Long memberId, JourCreateReqDto dto) {
       Member member = memberValidateService.findById(memberId);
       Team opponentTeam = teamvalidateService.validateTeam(dto.getOpponentTeamSC());



    }
}
