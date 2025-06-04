package com.inninglog.inninglog.journal.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.dto.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.JourCreateResDto;
import com.inninglog.inninglog.journal.repository.JournalRepository;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final JournalRepository repository;
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

        Journal journal = Journal.builder()
                .member(member)
                .date(dto.getDate())
                .opponentTeam(opponentTeam)
                .ourScore(dto.getOurScore())
                .theirScore(dto.getTheirScore())
                .resultScore(dto.getResultScore())
                .emotion(dto.getEmotion())
                .review_text(dto.getReview_text())
                .media_url(mediaUrl)
                .is_public(dto.getIs_public())
                .stadium(stadium)
                .build();


        repository.save(journal);

        return journal;
    }
}
