package com.inninglog.inninglog.domain.journal.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.domain.journal.repository.JournalRepository;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.team.domain.Team;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final JournalRepository journalRepository;

    //직관 일지 내용 업로드
    @Transactional
    public Journal createJournal(JourCreateReqDto dto, Member member, Team opponentTeam, Stadium stadium) {

        Journal journal = Journal.from(dto, member, opponentTeam, stadium);
        journalRepository.save(journal);
        log.info("📌직관 일지 저장 완료: journalId={}", journal.getId());

        return journal;
    }

    //직관 일지 목록 조회(캘린더)
    @Transactional(readOnly = true)
    public List<Journal> getJournalsByMemberCal(Member member, ResultScore resultScore) {
        List<Journal> journals = (resultScore != null) ?
                journalRepository.findAllByMemberAndResultScore(member, resultScore) :
                journalRepository.findAllByMember(member);

        return journals;
    }

    //직관 일지 목록 조회(모아보기)
    @Transactional(readOnly = true)
    public Page<Journal> getJournalsByMemberSum(Member member, Pageable pageable, ResultScore resultScore) {
        //승무패 필터링일경우
        Page<Journal> journals;
        if (resultScore != null) {
            journals = journalRepository.findAllByMemberAndResultScore(member, resultScore, pageable);
        } else { //전체 보기일 경우
            journals = journalRepository.findAllByMember(member, pageable);
        }
        log.info("📌조회된 일지 개수: {}", journals.getTotalElements());

        return journals;
    }

    //특정 직관 일지 수정
    @Transactional
    public void accessToJournal(Long memberId, Long journalMemberId) {
        if (!journalMemberId.equals(memberId)) {
            log.warn("⚠️ 접근 권한 없음: memberId={}, journalOwnerId={}", memberId, journalMemberId);
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }
    }

    //직관 일지 업데이트
    @Transactional
    public void updateJournal(Journal journal, JourUpdateReqDto dto){
        journal.updateFrom(dto);
        log.info("📌 일지 수정 완료: journalId={}", journal.getId());
    }
}
