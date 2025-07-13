package com.inninglog.inninglog.journal.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.journal.dto.res.*;
import com.inninglog.inninglog.journal.repository.JournalRepository;
import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.gameSchdule.GameSchResDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.kbo.service.GameReportService;
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
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalService {

    private final GameReportService gameReportService;
    private final JournalRepository journalRepository;
    private final MemberRepository memberRepository;
    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;
    private final GameRepository gameRepository;

    //직관 일지 내용 업로드
    @Transactional
    public JourCreateResDto createJournal(Long memberId, JourCreateReqDto dto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Team opponentTeam = teamRepository.findByShortCode(dto.getOpponentTeamShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        Stadium stadium = stadiumRepository.findByShortCode(dto.getStadiumShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.STADIUM_NOT_FOUND));

        Journal journal = Journal.from(dto, member, opponentTeam, stadium);
        journalRepository.save(journal);

        gameReportService.createVisitedGame(memberId, dto.getGameId(), journal.getId());

        return JourCreateResDto.from(journal);
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


    //일지 기본 정보 제공
    @Transactional(readOnly = true)
    public JourGameResDto infoJournal(Long memberId, String gameId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new CustomException(ErrorCode.GAME_NOT_FOUND));


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

        Team team = teamRepository.findById(opponentTeamId)
                        .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        return JourGameResDto.fromGame(member.getTeam().getShortCode(), team.getShortCode(), game );
    }

    //유저의 응원팀 경기 일정 - 월기준 -홈에서 쓰기
    @Transactional(readOnly = true)
    public List<GameSchResDto> getGameSch(Long memberId, LocalDate startDate, LocalDate endDate) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long supportTeamId = member.getTeam().getId();

        List<Game> games = gameRepository.findByTeamAndDateRange(supportTeamId, startDate, endDate);

        return GameSchResDto.listFrom(games, supportTeamId);
    }

    //해당 일자의 경기 가져오기
    @Transactional(readOnly = true)
    public GameSchResDto getSingleGameSch(Long memberId, LocalDate gameDate) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Long supportTeamId = member.getTeam().getId();

        Game game = gameRepository.findByDateAndTeamId(gameDate, supportTeamId);

        if(game==null) return null;

        return GameSchResDto.from(game, supportTeamId);
    }


    //특정 직관 일지 조회
    @Transactional(readOnly = true)
    public JourUpdateResDto getDetailJournal(Long memberId, Long journalId){

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNAL_NOT_FOUND));

        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal);
        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());
    }

    //특정 직관 일지 수정
    @Transactional
    public JourUpdateResDto updateJournal(Long memberId, Long journalId, JourUpdateReqDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Journal journal = journalRepository.findById(journalId)
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNAL_NOT_FOUND));

        if (!journal.getMember().getId().equals(memberId)) {
            throw new CustomException(ErrorCode.UNAUTHORIZED_ACCESS);
        }

        journal.updateFrom(dto);

        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal);

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());

    }

}
