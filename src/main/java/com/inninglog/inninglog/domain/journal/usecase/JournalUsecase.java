package com.inninglog.inninglog.domain.journal.usecase;

import com.inninglog.inninglog.domain.journal.dto.res.JourCreateResDto;
import com.inninglog.inninglog.domain.journal.service.JournalService;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class JournalUsecase {

    private final JournalService journalService;
    private final MemberValidateService memberValidateService;

    @Transactional
    public JourCreateResDto createJournal(Long memberId, JourCreateResDto dto) {
        memberValidateService.findById(memberId);


    }
}
