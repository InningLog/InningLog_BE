package com.inninglog.inninglog.seatView.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.repository.JournalRepository;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.seatView.domain.*;
import com.inninglog.inninglog.seatView.dto.req.SeatCreateReqDto;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import com.inninglog.inninglog.seatView.dto.res.SeatCreateResDto;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.repository.*;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SeatViewService {

    private final MemberRepository memberRepository;
    private final JournalRepository journalRepository;
    private final StadiumRepository stadiumRepository;
    private final ZoneRepository zoneRepository;
    private final SeatViewRepository seatViewRepository;
    private final SeatViewEmotionTagRepository seatViewEmotionTagRepository;
    private final SeatViewEmotionTagMapRepository seatViewEmotionTagMapRepository;
    private final S3Uploader s3Uploader;

    /**
     * 좌석 시야 정보 작성
     */
    @Transactional
    public SeatCreateResDto createSeatView(Long memberId, SeatCreateReqDto dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("❌ [createSeatView] memberId={} 존재하지 않는 사용자 ID", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        Journal journal = journalRepository.findById(dto.getJournalId())
                .orElseThrow(() -> {
                    log.warn("❌ [createSeatView] journalId={} 존재하지 않는 직관 일지 ID", dto.getJournalId());
                    return new CustomException(ErrorCode.JOURNAL_NOT_FOUND);
                });

        if (journal.getSeatView() != null) {
            log.warn("⚠️ [createSeatView] journalId={} 이미 좌석 시야가 등록된 일지 ID", dto.getJournalId());
            throw new CustomException(ErrorCode.SEATVIEW_ALREADY_EXISTS);
        }

        Stadium stadium = stadiumRepository.findByShortCode(dto.getStadiumShortCode())
                .orElseThrow(() -> {
                    log.warn("❌ [createSeatView] stadiumShortCode={} 존재하지 않는 구장 코드", dto.getStadiumShortCode());
                    return new CustomException(ErrorCode.STADIUM_NOT_FOUND);
                });

        Zone zone = zoneRepository.findByShortCode(dto.getZoneShortCode())
                .orElseThrow(() -> {
                    log.warn("❌ [createSeatView] zoneShortCode={} 존재하지 않는 존 코드", dto.getZoneShortCode());
                    return new CustomException(ErrorCode.ZONE_NOT_FOUND);
                });

        SeatView seatView = SeatView.from(dto, member, journal, stadium, zone);
        journal.setSeatView(seatView);
        seatViewRepository.save(seatView);

        // 감정 태그 매핑 저장
        for (String code : dto.getEmotionTagCodes()) {
            SeatViewEmotionTag emotionTag = seatViewEmotionTagRepository.findByCode(code)
                    .orElseThrow(() -> {
                        log.warn("❌ [createSeatView] emotionTagCode={} 존재하지 않는 감정 태그 코드", code);
                        return new CustomException(ErrorCode.EMOTION_TAG_NOT_FOUND);
                    });

            SeatViewEmotionTagMap map = SeatViewEmotionTagMap.builder()
                    .seatView(seatView)
                    .seatViewEmotionTag(emotionTag)
                    .build();

            seatViewEmotionTagMapRepository.save(map);
        }

        log.info("✅ [createSeatView] seatViewId={}, memberId={} 좌석 시야 등록 완료", seatView.getId(), memberId);
        return SeatCreateResDto.from(seatView);
    }

    /**
     * 특정 좌석 시야 조회
     */
    /**
     * 특정 좌석 시야 조회
     */
    public SeatViewDetailResult getSeatView(Long memberId, Long seatViewId) {
        memberRepository.findById(memberId)
                .orElseThrow(() -> {
                    log.warn("❌ [getSeatView] memberId={} 존재하지 않는 사용자 ID", memberId);
                    return new CustomException(ErrorCode.USER_NOT_FOUND);
                });

        SeatView seatView = seatViewRepository.findById(seatViewId)
                .orElseThrow(() -> {
                    log.warn("❌ [getSeatView] seatViewId={} 존재하지 않는 좌석 시야 ID", seatViewId);
                    return new CustomException(ErrorCode.SEATVIEW_NOT_FOUND);
                });

        String presignedUrl = s3Uploader.generatePresignedGetUrl(seatView.getView_media_url());

        // 감정 태그 조회 (단일 좌석 기준)
        List<SeatViewEmotionTagDto> emotionTags = seatViewEmotionTagRepository
                .findDtosBySeatViewId(seatViewId);

        log.info("📌 [getSeatView] seatViewId={}, memberId={} 좌석 시야 조회 성공", seatViewId, memberId);

        return SeatViewDetailResult.from(
                seatView,
                presignedUrl,
                seatView.getZone().getName(),
                seatView.getZone().getShortCode(),
                seatView.getSection(),
                seatView.getSeatRow(),
                seatView.getZone().getStadium().getName(),
                emotionTags
        );
    }
}