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
import java.util.stream.Collectors;

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
    public List<JournalCalListResDto> getJournalsByMemberCal(Long memberId, ResultScore resultScore) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [getJournalsByMemberCal] 존재하지 않는 사용자: memberId={}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        List<Journal> journals = (resultScore != null) ?
                journalRepository.findAllByMemberAndResultScore(member, resultScore) :
                journalRepository.findAllByMember(member);

        log.info("📌 [getJournalsByMemberCal] 조회된 일지 개수: {}", journals.size());

        return journals.stream()
                .map(JournalCalListResDto::from)
                .collect(Collectors.toList());
    }


    //직관 일지 목록 조회(모아보기)
    @Transactional(readOnly = true)
    public Page<JournalSumListResDto> getJournalsByMemberSum(Long memberId, Pageable pageable, ResultScore resultScore) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [getJournalsByMemberSum] 존재하지 않는 사용자: memberId={}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Page<Journal> journals;

        //승무패 필터링일경우
        if (resultScore != null) {
            journals = journalRepository.findAllByMemberAndResultScore(member, resultScore, pageable);
        } else { //전체 보기일 경우
            journals = journalRepository.findAllByMember(member, pageable);
        }

        log.info("📌 [getJournalsByMemberSum] 조회된 일지 개수: {}", journals.getTotalElements());

        Page<JournalSumListResDto> dtoPage = journals.map(
                journal -> JournalSumListResDto.from(journal, s3Uploader.generatePresignedGetUrl(journal.getMedia_url()), member.getTeam().getShortCode())
        );

        return dtoPage;
    }


    //일지 기본 정보 제공
    @Transactional(readOnly = true)
    public JourGameResDto infoJournal(Long memberId, String gameId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [infoJournal] 존재하지 않는 사용자: memberId={}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [infoJournal] 존재하지 않는 경기: gameId={}", gameId);
                    return new CustomException(ErrorCode.GAME_NOT_FOUND);
                });


        Long supportTeamId = member.getTeam().getId();

        Long opponentTeamId = 0L;

        //게임의 원정팀이 유저의 응원팀과 다를 경우
        if(!Objects.equals(game.getAwayTeam().getId(), supportTeamId)){
            //원정팀이 상대팀
             opponentTeamId = game.getAwayTeam().getId();
        }else {
            //게임의 원정팀이 유저의 응원팀과 같은 경우
            //상대팀은 홈팀이였다.
            opponentTeamId = game.getHomeTeam().getId();
        }

        log.info("📌 [infoJournal] 상대팀 ID 계산 완료: {}", opponentTeamId);

        final Long finalOpponentTeamId = opponentTeamId;

        Team team = teamRepository.findById(finalOpponentTeamId)
                        .orElseThrow(() -> {
                            log.warn("⚠️ [infoJournal] 존재하지 않는 팀: teamId={}", finalOpponentTeamId);
                            return new CustomException(ErrorCode.TEAM_NOT_FOUND);
                        });

        return JourGameResDto.fromGame(member.getTeam().getShortCode(), team.getShortCode(), game );
    }


    //해당 일자의 경기 가져오기
    @Transactional(readOnly = true)
    public GameSchResDto getSingleGameSch(Long memberId, LocalDate gameDate) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [getSingleGameSch] 존재하지 않는 사용자: memberId={}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Long supportTeamId = member.getTeam().getId();

        Game game = gameRepository.findByDateAndTeamId(gameDate, supportTeamId);

        log.info("📌 [getSingleGameSch] 조회된 경기: {}", game != null ? game.getGameId() : "없음");

        if (game == null) {
            log.warn("⚠️ [getSingleGameSch] 해당 날짜에 경기 없음: date={}, teamId={}", gameDate, supportTeamId);
            return null;
        }

        if(game==null) return null;

        return GameSchResDto.from(game, supportTeamId);
    }


    //특정 직관 일지 조회
    @Transactional(readOnly = true)
    public JourUpdateResDto getDetailJournal(Long memberId, Long journalId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [getDetailJournal] 존재하지 않는 사용자: memberId={}", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> {
                    log.warn("⚠️ [getDetailJournal] 존재하지 않는 일지: journalId={}", journalId);
                    return new CustomException(ErrorCode.JOURNAL_NOT_FOUND);
                });

        // 프리사인드 URL 생성
        String presignedUrl = s3Uploader.generatePresignedGetUrl(journal.getMedia_url());

        log.info("📌 [getDetailJournal] 프리사인드 URL 생성 완료: {}", presignedUrl);

        // Presigned URL을 포함해 DTO 생성
        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal, presignedUrl);

        if(journal.getSeatView() == null){
            return JourUpdateResDto.from(jourDetailResDto, null);
        }

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());
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
