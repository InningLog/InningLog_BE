package com.inninglog.inninglog.domain.journal.service;

import com.inninglog.inninglog.domain.journal.dto.res.*;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.domain.journal.repository.JournalRepository;
import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.dto.gameSchdule.GameSchResDto;
import com.inninglog.inninglog.domain.kbo.repository.GameRepository;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class JournalService {

    private final GameReportService gameReportService;
    private final JournalRepository journalRepository;
    private final MemberRepository memberRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;
    private final S3Uploader s3Uploader;

    //직관 일지 내용 업로드
    @Transactional
    public Journal createJournal(JourCreateReqDto dto, Member member, Team opponentTeam, Stadium stadium) {

        Journal journal = Journal.from(dto, member, opponentTeam, stadium);
        journalRepository.save(journal);
        log.info("📌 [createJournal] 직관 일지 저장 완료: journalId={}", journal.getId());

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
        log.info("📌 [getJournalsByMemberSum] 조회된 일지 개수: {}", journals.getTotalElements());

        return journals;
    }


    //특정 직관 일지 수정
    @Transactional
    public JourUpdateResDto updateJournal(Long memberId, Long journalId, JourUpdateReqDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [updateJournal] 존재하지 않는 사용자: memberId={}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [updateJournal] 존재하지 않는 일지: journalId={}", journalId);
                    return new CustomException(ErrorCode.JOURNAL_NOT_FOUND);
                });

        if (!journal.getMember().getId().equals(memberId)) {
            log.warn("⚠️ [updateJournal] 접근 권한 없음: memberId={}, journalOwnerId={}", memberId, journal.getMember().getId());
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        journal.updateFrom(dto);

        log.info("📌 [updateJournal] 일지 수정 완료: journalId={}", journal.getId());

        // 프리사인드 URL 생성
        String presignedUrl = s3Uploader.generatePresignedGetUrl(journal.getMedia_url());

        // Presigned URL을 포함해 DTO 생성
        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal, presignedUrl);

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());

    }

}
