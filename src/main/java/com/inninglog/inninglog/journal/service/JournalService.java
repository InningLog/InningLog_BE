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

    //직관 일지 생성
    @Transactional
    public Journal createJournal(Long memberId, JourCreateReqDto dto, MultipartFile file){
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Team opponentTeam = teamRepository.findByShortCode(dto.getOpponentTeamShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        Stadium stadium = stadiumRepository.findByShortCode(dto.getStadiumShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.STADIUM_NOT_FOUND));

        String mediaUrl = null;
        if (file != null && !file.isEmpty()) {
            try {
                mediaUrl = s3Uploader.uploadFile(file, "journal");
            } catch (IOException e) {
                throw new RuntimeException("S3 업로드 실패", e);
            }
        }

        //경기 결과 계산 (백엔드 자동 처리)
        ResultScore resultScore;

        if (dto.getOurScore() > dto.getTheirScore()) {
            resultScore = ResultScore.WIN;
        } else if (dto.getOurScore() < dto.getTheirScore()) {
            resultScore = ResultScore.LOSE;
        } else {
            resultScore = ResultScore.DRAW;
        }

        Journal journal = Journal.builder()
                .member(member)
                .date(dto.getDate())
                .opponentTeam(opponentTeam)
                .ourScore(dto.getOurScore())
                .theirScore(dto.getTheirScore())
                .resultScore(resultScore)
                .emotion(dto.getEmotion())
                .review_text(dto.getReview_text())
                .media_url(mediaUrl)
                .is_public(dto.getIs_public())
                .stadium(stadium)
                .build();


        journalRepository.save(journal);

        return journal;
    }


    //직관 일지 목록 조회(캘린더)
    @Transactional(readOnly = true)
    public List<JournalCalListResDto> getJournalsByMemberCal(Long memberId, ResultScore resultScore) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        List<Journal> journals;

        if (resultScore != null) {
            journals = journalRepository.findAllByMemberAndResultScore(member, resultScore);
        } else {
            journals = journalRepository.findAllByMember(member);
        }

        return journals.stream()
                .map(journal -> new JournalCalListResDto(
                        journal.getId(),
                        journal.getOurScore(),
                        journal.getTheirScore(),
                        journal.getResultScore(),
                        journal.getDate(),
                        journal.getOpponentTeam().getName(),
                        journal.getStadium().getName()
                ))
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

        return journals.map(journal -> new JournalSumListResDto(
                journal.getId(),
                journal.getMedia_url(),
                journal.getResultScore(),
                journal.getDate(),
                journal.getOpponentTeam().getName(),
                journal.getStadium().getName()
        ));
    }

}
