package com.inninglog.inninglog.journal.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.journal.dto.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.JournalCalListResDto;
import com.inninglog.inninglog.journal.dto.JournalSumListResDto;
import com.inninglog.inninglog.journal.repository.JournalRepository;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository journalRepository;
    private final MemberRepository memberRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;
    private final S3Uploader s3Uploader;

    //S3 업로드
    @Transactional
    public String uploadImage(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new CustomException(ErrorCode.FILE_IS_EMPTY);
        }

        try {
            return s3Uploader.uploadFile(file, "journal");
        } catch (IOException e) {
            throw new CustomException(ErrorCode.S3_UPLOAD_FAILED);
        }
    }


    //직관 일지 내용 업로드
    @Transactional
    public Journal createJournal(Long memberId, JourCreateReqDto dto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Team opponentTeam = teamRepository.findByShortCode(dto.getOpponentTeamShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        Stadium stadium = stadiumRepository.findByShortCode(dto.getStadiumShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.STADIUM_NOT_FOUND));

        Journal journal = Journal.from(dto, member, opponentTeam, stadium);
        journalRepository.save(journal);

        return journal;
    }


    //직관 일지 목록 조회(캘린더)
    @Transactional(readOnly = true)
    public List<JournalCalListResDto> getJournalsByMemberCal(Long memberId, ResultScore resultScore) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Journal> journals = (resultScore != null) ?
                journalRepository.findAllByMemberAndResultScore(member, resultScore) :
                journalRepository.findAllByMember(member);

        return journals.stream()
                .map(JournalCalListResDto::from)
                .collect(Collectors.toList());
    }


    //직관 일지 목록 조회(모아보기)
    @Transactional(readOnly = true)
    public Page<JournalSumListResDto> getJournalsByMemberSum(Long memberId, Pageable pageable, ResultScore resultScore) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Page<Journal> journals;

        //승무패 필터링일경우
        if (resultScore != null) {
            journals = journalRepository.findAllByMemberAndResultScore(member, resultScore, pageable);
        } else { //전체 보기일 경우
            journals = journalRepository.findAllByMember(member, pageable);
        }

        return journals.map(JournalSumListResDto::from);
    }

}
