package com.inninglog.inninglog.seatView.service;

import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTag;
import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTagMap;
import com.inninglog.inninglog.seatView.dto.req.HashtagSearchReq;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import com.inninglog.inninglog.seatView.dto.res.HashtagSearchRes;
import com.inninglog.inninglog.seatView.dto.res.SeatInfo;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.dto.res.SeatViewImageResult;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagMapRepository;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagRepository;
import com.inninglog.inninglog.seatView.repository.SeatViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class HashtagSearchService {

    private final SeatViewRepository seatViewRepository;
    private final SeatViewEmotionTagRepository emotionTagRepository;
    private final SeatViewEmotionTagMapRepository emotionTagMapRepository;

    // 모아보기 형태 검색 (사진만)
    public HashtagSearchRes searchSeatViewsByHashtagsGallery(
            String stadiumShortCode,
            List<String> hashtagCodes,
            Boolean isAndCondition
    ) {
        HashtagSearchReq request = HashtagSearchReq.from(
                stadiumShortCode, hashtagCodes, isAndCondition
        );

        // 검증
        if (!request.isValidRequest()) {
            throw new IllegalArgumentException("해시태그는 최소 1개, 최대 2개까지 선택할 수 있습니다.");
        }

        // 조회 분기
        List<SeatView> seatViews = request.isAndCondition()
                ? seatViewRepository.findSeatViewsByHashtagsAnd(
                request.getStadiumShortCode(),
                request.getHashtagCodes(),
                request.getHashtagCodes().size()
        )
                : seatViewRepository.findSeatViewsByHashtagsOr(
                request.getStadiumShortCode(),
                request.getHashtagCodes()
        );

        // 변환
        List<SeatViewImageResult> results = seatViews.stream()
                .map(sv -> SeatViewImageResult.builder()
                        .seatViewId(sv.getId())
                        .viewMediaUrl(sv.getView_media_url())
                        .build())
                .collect(Collectors.toList());

        String searchSummary = generateHashtagSearchSummary(request, seatViews);

        return HashtagSearchRes.builder()
                .searchSummary(searchSummary)
                .seatViews(results)
                .totalCount(results.size())
                .isGalleryView(true)
                .build();
    }

    // 게시물 형태 검색 (상세 정보 포함)
    public List<SeatViewDetailResult> searchSeatViewsByHashtagsDetail(
            String stadiumShortCode,
            List<String> hashtagCodes,
            Boolean isAndCondition
    ) {
        HashtagSearchReq request = HashtagSearchReq.from(
                stadiumShortCode, hashtagCodes, isAndCondition
        );

        // 요청 검증
        if (!request.isValidRequest()) {
            throw new IllegalArgumentException("해시태그는 최소 1개, 최대 2개까지 선택할 수 있습니다.");
        }

        // 좌석 시야 데이터 조회 (상세 정보 포함, AND/OR 조건에 따라 분기)
        List<SeatView> seatViews = request.isAndCondition()
                ? seatViewRepository.findSeatViewsByHashtagsWithDetailsAnd(
                request.getStadiumShortCode(),
                request.getHashtagCodes(),
                request.getHashtagCodes().size()
        )
                : seatViewRepository.findSeatViewsByHashtagsWithDetailsOr(
                request.getStadiumShortCode(),
                request.getHashtagCodes()
        );

        // 감정 태그 데이터 조회
        List<Long> seatViewIds = seatViews.stream()
                .map(SeatView::getId)
                .collect(Collectors.toList());

        Map<Long, List<SeatViewEmotionTagDto>> emotionTagMap = getEmotionTagMap(seatViewIds);

        // 게시물 형태 결과 변환
        return seatViews.stream()
                .map(sv -> SeatViewDetailResult.from(sv, emotionTagMap.getOrDefault(sv.getId(), new ArrayList<>())))
                .collect(Collectors.toList());
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

    private String generateHashtagSearchSummary(HashtagSearchReq request, List<SeatView> seatViews) {
        StringBuilder summary = new StringBuilder();

        // 구장 정보
        if (!seatViews.isEmpty()) {
            summary.append(seatViews.get(0).getStadium().getName()).append(" ");
        }

        // 해시태그 정보
        List<SeatViewEmotionTag> tags = emotionTagRepository.findByCodeIn(request.getHashtagCodes());
        List<String> tagLabels = tags.stream()
                .map(SeatViewEmotionTag::getLabel)
                .collect(Collectors.toList());

        summary.append("'").append(String.join(", ", tagLabels)).append("'");

        // AND/OR 조건 표시
        if (request.getHashtagCodes().size() > 1) {
            if (request.isAndCondition()) {
                summary.append(" (모든 태그 포함)");
            } else {
                summary.append(" (태그 중 하나 이상 포함)");
            }
        }

        summary.append(" 해시태그 검색 결과");

        return summary.toString();
    }
}