package com.inninglog.inninglog.domain.journal.usecase;

import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.res.JourCreateResDto;
import com.inninglog.inninglog.domain.journal.dto.res.JournalCalListResDto;
import com.inninglog.inninglog.domain.journal.dto.res.JournalSumListResDto;
import com.inninglog.inninglog.domain.journal.service.JournalService;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.stadium.service.StadiumValidateService;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.service.TeamvalidateService;
import com.inninglog.inninglog.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalUsecase {

    private final JournalService journalService;
    private final MemberValidateService memberValidateService;
    private final TeamvalidateService teamvalidateService;
    private final StadiumValidateService stadiumValidateService;
    private final GameReportService gameReportService;
    private final S3Uploader s3Uploader;


    //직관 일지 생성
    @Transactional
    public JourCreateResDto createJournal(Long memberId, JourCreateReqDto dto) {
       Member member = memberValidateService.findById(memberId);
       Team opponentTeam = teamvalidateService.validateTeam(dto.getOpponentTeamSC());
       Stadium stadium = stadiumValidateService.validateStadium(dto.getStadiumSC());

       Journal journal = journalService.createJournal(dto, member, opponentTeam, stadium);
       gameReportService.createVisitedGame(member.getId(), dto.getGameId(),journal.getId());

       return JourCreateResDto.from(journal);
    }

    //직관 일지 조회 - 캘린더
    @Transactional(readOnly = true)
    public List<JournalCalListResDto> getJournalsByMemberCal(Long memberId, ResultScore resultScore) {
        Member member = memberValidateService.findById(memberId);
        List<Journal> journals = journalService.getJournalsByMemberCal(member, resultScore);

        return journals.stream()
                .map(JournalCalListResDto::from)
                .collect(Collectors.toList());
    }

    //직관 일지 조회 - 모아보기
    @Transactional(readOnly = true)
    public Page<JournalSumListResDto> getJournalsByMemberSum(
            Long memberId, Pageable pageable, ResultScore resultScore) {
        Member member = memberValidateService.findById(memberId);
        Page<Journal> journals = journalService.getJournalsByMemberSum(member, pageable, resultScore);

        Page<JournalSumListResDto> dtoPage = journals.map(
                journal -> JournalSumListResDto.from(journal, s3Uploader.generatePresignedGetUrl(journal.getMedia_url()), member.getTeam().getShortCode())
        );

        return dtoPage;

    }
}
