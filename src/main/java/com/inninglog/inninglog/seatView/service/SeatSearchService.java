package com.inninglog.inninglog.seatView.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTagMap;
import com.inninglog.inninglog.seatView.dto.req.SeatSearchReq;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagMapRepository;
import com.inninglog.inninglog.seatView.repository.SeatViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class SeatSearchService {

    private final SeatViewRepository seatViewRepository;
    private final SeatViewEmotionTagMapRepository emotionTagMapRepository;
    private final S3Uploader s3Uploader;
    private final MemberRepository memberRepository;

    public Page<SeatViewDetailResult> searchSeats(
            Long memeberId,
            String stadiumShortCode,
            String zoneShortCode,
            String section,
            String seatRow,
            Pageable pageable
    ) {
        memberRepository.findById(memeberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SeatSearchReq request = SeatSearchReq.from(stadiumShortCode, zoneShortCode, section, seatRow);

        if (!request.isValidRequest()) {
            log.warn("❌ [searchSeats] stadium={}, zone={}, section={}, seatRow={} 잘못된 좌석 검색 요청",
                    stadiumShortCode, zoneShortCode, section, seatRow);
            throw new CustomException(ErrorCode.INVALID_SEAT_SEARCH);
        }

        log.info("🔍 [searchSeats] stadium={}, zone={}, section={}, seatRow={}, page={} 좌석 검색 요청",
                stadiumShortCode, zoneShortCode, section, seatRow, pageable.getPageNumber());

        Page<SeatView> seatViews = seatViewRepository.findSeatViewsBySearchCriteriaPageable(
                request.getStadiumShortCode(),
                request.getZoneShortCode(),
                request.getSection(),
                request.getSeatRow(),
                pageable
        );

        List<Long> seatViewIds = seatViews.stream()
                .map(SeatView::getId)
                .toList();

        Map<Long, List<SeatViewEmotionTagDto>> emotionTagMap = getEmotionTagMap(seatViewIds);

        log.info("✅ [searchSeats] seatCount={} 검색된 좌석 수", seatViewIds.size());

        return seatViews.map(sv -> {
            String presignedUrl = s3Uploader.generatePresignedGetUrl(sv.getView_media_url());
            List<SeatViewEmotionTagDto> emotionTags = emotionTagMap.getOrDefault(sv.getId(), List.of());

            return SeatViewDetailResult.from(
                    sv,
                    presignedUrl,
                    sv.getZone().getName(),
                    sv.getZone().getShortCode(),
                    sv.getSection(),
                    sv.getSeatRow(),
                    sv.getZone().getStadium().getName(),
                    emotionTags
            );
        });
    }

    private Map<Long, List<SeatViewEmotionTagDto>> getEmotionTagMap(List<Long> seatViewIds) {
        if (seatViewIds.isEmpty()) {
            return Map.of();
        }

        List<SeatViewEmotionTagMap> tagMaps = emotionTagMapRepository.findBySeatViewIds(seatViewIds);

        return tagMaps.stream()
                .collect(Collectors.groupingBy(
                        tagMap -> tagMap.getSeatView().getId(),
                        Collectors.mapping(
                                tagMap -> SeatViewEmotionTagDto.builder()
                                        .code(tagMap.getSeatViewEmotionTag().getCode())
                                        .label(tagMap.getSeatViewEmotionTag().getLabel())
                                        .build(),
                                Collectors.toList()
                        )
                ));
    }
}