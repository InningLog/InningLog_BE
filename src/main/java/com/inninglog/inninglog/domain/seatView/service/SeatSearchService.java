package com.inninglog.inninglog.domain.seatView.service;

import com.inninglog.inninglog.domain.contentImage.domain.ContentImage;
import com.inninglog.inninglog.domain.contentImage.repository.ContentImageRepository;
import com.inninglog.inninglog.domain.contentType.ContentType;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.seatView.domain.SeatView;
import com.inninglog.inninglog.domain.seatView.dto.req.SeatSearchReq;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatViewImageResult;
import com.inninglog.inninglog.domain.seatView.repository.SeatViewRepository;
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
    private final ContentImageRepository contentImageRepository;
    private final MemberRepository memberRepository;

    public Page<SeatViewImageResult> searchSeats(
            Long memeberId,
            String stadiumShortCode,
            String section,
            String seatRow,
            Pageable pageable
    ) {
        memberRepository.findById(memeberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SeatSearchReq request = SeatSearchReq.from(stadiumShortCode, section, seatRow);

        if (!request.isValidRequest()) {
            log.warn("❌ [searchSeats] stadium={}, section={}, seatRow={} 잘못된 좌석 검색 요청",
                    stadiumShortCode, section, seatRow);
            throw new CustomException(ErrorCode.INVALID_SEAT_SEARCH);
        }

        log.info("🔍 [searchSeats] stadium={}, section={}, seatRow={}, page={} 좌석 검색 요청",
                stadiumShortCode, section, seatRow, pageable.getPageNumber());

        Page<SeatView> seatViews = seatViewRepository.findSeatViewsBySearchCriteriaPageable(
                request.getStadiumShortCode(),
                request.getSection(),
                request.getSeatRow(),
                pageable
        );

        List<Long> seatViewIds = seatViews.stream()
                .map(SeatView::getId)
                .toList();

        Map<Long, String> thumbnailMap = getThumbnailMap(seatViewIds);

        log.info("✅ [searchSeats] seatCount={} 검색된 좌석 수", seatViewIds.size());

        return seatViews.map(sv -> SeatViewImageResult.builder()
                .seatViewId(sv.getId())
                .viewMediaUrl(thumbnailMap.get(sv.getId()))
                .build());
    }

    private Map<Long, String> getThumbnailMap(List<Long> seatViewIds) {
        if (seatViewIds.isEmpty()) {
            return Map.of();
        }

        List<ContentImage> images = contentImageRepository.findAllByContentTypeAndTargetIdIn(
                ContentType.SEATVIEW, seatViewIds);

        return images.stream()
                .collect(Collectors.toMap(
                        ContentImage::getTargetId,
                        ContentImage::getOriginalUrl,
                        (existing, replacement) -> existing // 첫 번째 이미지(썸네일) 유지
                ));
    }
}
