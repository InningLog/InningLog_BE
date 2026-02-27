package com.inninglog.inninglog.domain.journal.usecase;

import com.inninglog.inninglog.domain.comment.service.CommentGetService;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.domain.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.domain.journal.dto.res.*;
import com.inninglog.inninglog.global.dto.SliceResponse;
import com.inninglog.inninglog.domain.journal.service.JournalGetService;
import com.inninglog.inninglog.domain.journal.service.JournalService;
import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.dto.gameSchdule.GameSchResDto;
import com.inninglog.inninglog.domain.kbo.service.GameReportService;
import com.inninglog.inninglog.domain.kbo.service.GameGetService;
import com.inninglog.inninglog.domain.like.service.LikeValidateService;
import com.inninglog.inninglog.domain.scrap.service.ScrapValidateService;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.service.MemberValidateService;
import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.stadium.service.StadiumValidateService;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.service.TeamGetService;
import com.inninglog.inninglog.global.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Slice;
import org.springframework.data.domain.SliceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class JournalUsecase {

    private final JournalService journalService;
    private final JournalGetService journalGetService;
    private final MemberValidateService memberValidateService;
    private final TeamGetService teamGetService;
    private final StadiumValidateService stadiumValidateService;
    private final GameReportService gameReportService;
    private final GameGetService gameGetService;
    private final S3Uploader s3Uploader;
    private final LikeValidateService likeValidateService;
    private final ScrapValidateService scrapValidateService;
    private final CommentGetService commentGetService;


    //직관 일지 생성
    @Transactional
    public JourCreateResDto createJournal(Long memberId, JourCreateReqDto dto) {
       Member member = memberValidateService.findById(memberId);
       Team opponentTeam = teamGetService.validateTeam(dto.getOpponentTeamSC());
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

        // N+1 최적화: 좋아요/스크랩 여부를 한 번에 조회
        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();
        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        return journals.map(
                journal -> JournalSumListResDto.from(
                        journal,
                        s3Uploader.getDirectUrl(journal.getMedia_url()),
                        member.getTeam().getShortCode(),
                        likedIds.contains(journal.getId()),
                        scrapedIds.contains(journal.getId())
                )
        );
    }

    //일지 기본 정보 제공
    @Transactional(readOnly = true)
    public JourGameResDto infoPreJournal(Long memberId, String gameId){
        Member member = memberValidateService.findById(memberId);
        Game game = gameGetService.findById(gameId);
        String opponentTeamSC = teamGetService.getOpponentTeamSC(member, game);

        return JourGameResDto.fromGame(member.getTeam().getShortCode(), opponentTeamSC, game );
    }

    //해당 일자의 경기 가져오기
    @Transactional(readOnly = true)
    public GameSchResDto getSingleGameSch(Long memberId, LocalDate gameDate) {
        Member member = memberValidateService.findById(memberId);
        Long supportTeamId = teamGetService.getSupportTeamId(member);
        Game game = gameGetService.findByDateAndTeamId(gameDate,supportTeamId);

        return GameSchResDto.from(game, supportTeamId);
    }

    //특정 직관 일지 조회
    @Transactional(readOnly = true)
    public JourUpdateResDto getDetailJournal(Long memberId, Long journalId){
        Member member = memberValidateService.findById(memberId);
        Journal journal = journalGetService.getJournalById(journalId);
        String mediaUrl = s3Uploader.getDirectUrl(journal.getMedia_url());
        boolean likedByMe = likeValidateService.likedByMe(ContentType.JOURNAL, journalId, member);
        boolean scrapedByMe = scrapValidateService.scrapedByMe(ContentType.JOURNAL, journalId, member);
        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal, mediaUrl, likedByMe, scrapedByMe);
        if(journal.getSeatView() == null){
            return JourUpdateResDto.from(jourDetailResDto, null);
        }

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());
    }

    //특정 직관 일지 수정
    @Transactional
    public JourUpdateResDto updateJournal(Long memberId, Long journalId, JourUpdateReqDto dto) {
        Member member = memberValidateService.findById(memberId);
        Journal journal = journalGetService.getJournalById(journalId);
        journalService.accessToJournal(memberId, journal.getMember().getId());
        journalService.updateJournal(journal, dto);
        String mediaUrl = s3Uploader.getDirectUrl(journal.getMedia_url());
        boolean likedByMe = likeValidateService.likedByMe(ContentType.JOURNAL, journalId, member);
        boolean scrapedByMe = scrapValidateService.scrapedByMe(ContentType.JOURNAL, journalId, member);

        JourDetailResDto jourDetailResDto = JourDetailResDto.from(member, journal, mediaUrl, likedByMe, scrapedByMe);

        return JourUpdateResDto.from(jourDetailResDto, journal.getSeatView().getId());
    }

    //공개 일지 피드 조회
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> getPublicJournalFeed(Long memberId, String teamShortCode, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Journal> journals;

        // teamShortCode가 "ALL"이면 전체 조회, 그 외에는 팀별 조회
        if ("ALL".equalsIgnoreCase(teamShortCode)) {
            journals = journalGetService.getPublicJournals(pageable);
        } else {
            journals = journalGetService.getPublicJournalsByTeam(teamShortCode, pageable);
        }

        // N+1 최적화: 좋아요/스크랩 여부를 한 번에 조회
        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();

        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        Slice<JournalFeedResDto> dtoSlice = journals.map(journal -> {
            boolean writedByMe = journal.getMember().getId().equals(memberId);
            boolean likedByMe = likedIds.contains(journal.getId());
            boolean scrapedByMe = scrapedIds.contains(journal.getId());

            return JournalFeedResDto.from(
                    journal,
                    s3Uploader.getDirectUrl(journal.getMedia_url()),
                    writedByMe,
                    likedByMe,
                    scrapedByMe
            );
        });

        return SliceResponse.of(dtoSlice);
    }

    // 커뮤니티 검색: 공개 일지 키워드 검색 (팀별 필터링 지원)
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> searchPublicJournals(Long memberId, String keyword, String teamShortCode, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Journal> journals;
        if ("ALL".equalsIgnoreCase(teamShortCode)) {
            journals = journalGetService.searchPublicJournals(keyword, pageable);
        } else {
            journals = journalGetService.searchPublicJournalsByTeam(keyword, teamShortCode, pageable);
        }

        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();

        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        Slice<JournalFeedResDto> dtoSlice = journals.map(journal -> {
            boolean writedByMe = journal.getMember().getId().equals(memberId);
            boolean likedByMe = likedIds.contains(journal.getId());
            boolean scrapedByMe = scrapedIds.contains(journal.getId());

            return JournalFeedResDto.from(
                    journal,
                    s3Uploader.getDirectUrl(journal.getMedia_url()),
                    writedByMe,
                    likedByMe,
                    scrapedByMe
            );
        });

        return SliceResponse.of(dtoSlice);
    }

    // 마이페이지: 내가 쓴 직관 일지 목록
    @Transactional(readOnly = true)
    public SliceResponse<JournalSumListResDto> getMyJournals(Member member, Pageable pageable) {
        Slice<Journal> journals = journalGetService.getMyJournals(member, pageable);

        // N+1 최적화: 좋아요/스크랩 여부를 한 번에 조회
        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();
        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        Slice<JournalSumListResDto> dtoSlice = journals.map(
                journal -> JournalSumListResDto.from(
                        journal,
                        s3Uploader.getDirectUrl(journal.getMedia_url()),
                        member.getTeam().getShortCode(),
                        likedIds.contains(journal.getId()),
                        scrapedIds.contains(journal.getId())
                )
        );

        return SliceResponse.of(dtoSlice);
    }

    // 마이페이지: 내가 댓글 단 직관일지
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> getMyCommentedJournals(Long memberId, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Long> journalIdSlice = commentGetService.getCommentedContentIds(member, ContentType.JOURNAL, pageable);
        return toFeedPage(journalIdSlice, member, pageable);
    }

    // 마이페이지: 내가 스크랩한 직관일지
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> getMyScrappedJournals(Long memberId, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Long> journalIdSlice = scrapValidateService.getScrappedContentIds(member, ContentType.JOURNAL, pageable);
        return toFeedPage(journalIdSlice, member, pageable);
    }

    // 마이페이지: 내가 좋아요 누른 직관일지
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> getMyLikedJournals(Long memberId, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Long> journalIdSlice = likeValidateService.getLikedContentIds(member, ContentType.JOURNAL, pageable);
        return toFeedPage(journalIdSlice, member, pageable);
    }

    // 인기 직관일지 조회 (좋아요 10개 이상, 공개)
    @Transactional(readOnly = true)
    public SliceResponse<JournalFeedResDto> getPopularJournals(Long memberId, Pageable pageable) {
        Member member = memberValidateService.findById(memberId);
        Slice<Journal> journals = journalGetService.getPopularJournals(10L, pageable);

        List<Long> journalIds = journals.getContent().stream()
                .map(Journal::getId)
                .toList();

        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        Slice<JournalFeedResDto> dtoSlice = journals.map(journal -> {
            boolean writedByMe = journal.getMember().getId().equals(memberId);
            boolean likedByMe = likedIds.contains(journal.getId());
            boolean scrapedByMe = scrapedIds.contains(journal.getId());

            return JournalFeedResDto.from(
                    journal,
                    s3Uploader.getDirectUrl(journal.getMedia_url()),
                    writedByMe,
                    likedByMe,
                    scrapedByMe
            );
        });

        return SliceResponse.of(dtoSlice);
    }

    // ID Slice → JournalFeedResDto Slice 변환 헬퍼 (N+1 최적화)
    private SliceResponse<JournalFeedResDto> toFeedPage(Slice<Long> journalIdSlice, Member member, Pageable pageable) {
        List<Long> journalIds = journalIdSlice.getContent();

        if (journalIds.isEmpty()) {
            return SliceResponse.empty(pageable);
        }

        List<Journal> journals = journalGetService.findAllByIds(journalIds);
        Map<Long, Journal> journalMap = journals.stream()
                .collect(Collectors.toMap(Journal::getId, Function.identity()));

        Set<Long> likedIds = likeValidateService.findLikedTargetIds(ContentType.JOURNAL, journalIds, member);
        Set<Long> scrapedIds = scrapValidateService.findScrapedTargetIds(ContentType.JOURNAL, journalIds, member);

        List<JournalFeedResDto> dtos = journalIds.stream()
                .map(journalMap::get)
                .filter(journal -> journal != null)
                .map(journal -> {
                    boolean writedByMe = journal.getMember().getId().equals(member.getId());
                    boolean likedByMe = likedIds.contains(journal.getId());
                    boolean scrapedByMe = scrapedIds.contains(journal.getId());

                    return JournalFeedResDto.from(
                            journal,
                            s3Uploader.getDirectUrl(journal.getMedia_url()),
                            writedByMe,
                            likedByMe,
                            scrapedByMe
                    );
                })
                .toList();

        return SliceResponse.of(dtos, journalIdSlice.hasNext(), pageable);
    }
}
