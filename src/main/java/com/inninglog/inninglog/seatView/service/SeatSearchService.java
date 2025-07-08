package com.inninglog.inninglog.seatView.service;

import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTagMap;
import com.inninglog.inninglog.seatView.dto.req.SeatSearchReq;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import com.inninglog.inninglog.seatView.dto.res.SeatInfo;
import com.inninglog.inninglog.seatView.dto.res.SeatSearchRes;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagMapRepository;
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
public class SeatSearchService {

    private final SeatViewRepository seatViewRepository;
    private final SeatViewEmotionTagMapRepository emotionTagMapRepository;

    public SeatSearchRes searchSeats(
            String stadiumShortCode,
            String zoneShortCode,
            String section,
            String seatRow
    ) {
        SeatSearchReq request = SeatSearchReq.from(stadiumShortCode, zoneShortCode, section, seatRow);

        // 요청 검증
        if (!request.isValidRequest()) {
            throw new IllegalArgumentException("열 정보만으로는 검색할 수 없습니다. 최소 존 정보가 필요합니다.");
        }

        // 좌석 시야 데이터 조회
        List<SeatView> seatViews = seatViewRepository.findSeatViewsBySearchCriteria(
                request.getStadiumShortCode(),
                request.getZoneShortCode(),
                request.getSection(),
                request.getSeatRow()
        );

        // 감정 태그 조회
        List<Long> seatViewIds = seatViews.stream()
                .map(SeatView::getId)
                .toList();

        Map<Long, List<SeatViewEmotionTagDto>> emotionTagMap = getEmotionTagMap(seatViewIds);

        // 결과 변환
        List<SeatViewDetailResult> results = seatViews.stream()
                .map(sv -> SeatViewDetailResult.from(sv, emotionTagMap.getOrDefault(sv.getId(), new ArrayList<>())))
                .toList();

        String searchSummary = generateSearchSummary(request, seatViews);

        return SeatSearchRes.builder()
                .searchSummary(searchSummary)
                .seatViews(results)
                .totalCount(results.size())
                .build();
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

    private String generateSearchSummary(SeatSearchReq request, List<SeatView> seatViews) {
        StringBuilder summary = new StringBuilder();

        // 구장 정보는 항상 있다고 가정
        if (!seatViews.isEmpty()) {
            summary.append(seatViews.get(0).getStadium().getName()).append(" ");
        }

        // 존 정보
        if (request.getZoneShortCode() != null && !request.getZoneShortCode().trim().isEmpty()) {
            if (!seatViews.isEmpty()) {
                summary.append(seatViews.get(0).getZone().getName()).append(" ");
            }
        }

        // 구역 정보
        if (request.getSection() != null && !request.getSection().trim().isEmpty()) {
            summary.append(request.getSection()).append("구역 ");
        }

        // 열 정보
        if (request.getSeatRow() != null && !request.getSeatRow().trim().isEmpty()) {
            summary.append(request.getSeatRow()).append("열 ");
        }

        summary.append("검색 결과");

        return summary.toString();
    }

}