package com.inninglog.inninglog.seatView.service;

import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTag;
import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTagMap;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import com.inninglog.inninglog.seatView.dto.res.HashtagSearchRes;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.dto.res.SeatViewImageResult;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagMapRepository;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagRepository;
import com.inninglog.inninglog.seatView.repository.SeatViewRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
    private final SeatViewEmotionTagMapRepository emotionTagMapRepository;
    private final S3Uploader s3Uploader;


    // 모아보기 형태 검색 (사진만)
    public Page<SeatViewImageResult> searchSeatViewsByHashtagsGallery(String stadiumShortCode, List<String> hashtagCodes, Pageable pageable) {
        if (hashtagCodes == null || hashtagCodes.isEmpty() || hashtagCodes.size() > 5) {
            throw new IllegalArgumentException("해시태그는 최소 1개, 최대 5개까지 선택할 수 있습니다.");
        }

        Page<SeatView> seatViewPage = seatViewRepository.findSeatViewsByHashtagsAndPaged(
                stadiumShortCode,
                hashtagCodes,
                hashtagCodes.size(),
                pageable
        );

        return seatViewPage.map(sv -> SeatViewImageResult.builder()
                .seatViewId(sv.getId())
                .viewMediaUrl(s3Uploader.generatePresignedGetUrl(sv.getView_media_url()))
                .build());
    }

    // 게시물 형태 검색 (상세 정보 포함)
    public Page<SeatViewDetailResult> searchSeatViewsByHashtagsDetail(String stadiumShortCode, List<String> hashtagCodes, Pageable pageable) {
        if (hashtagCodes == null || hashtagCodes.isEmpty() || hashtagCodes.size() > 5) {
            throw new IllegalArgumentException("해시태그는 최소 1개, 최대 5개까지 선택할 수 있습니다.");
        }

        Page<SeatView> seatViewPage = seatViewRepository.findSeatViewsByHashtagsWithDetailsAndPaged(
                stadiumShortCode,
                hashtagCodes,
                hashtagCodes.size(),
                pageable
        );

        List<Long> seatViewIds = seatViewPage.getContent().stream()
                .map(SeatView::getId)
                .collect(Collectors.toList());

        Map<Long, List<SeatViewEmotionTagDto>> emotionTagMap = getEmotionTagMap(seatViewIds);

        Page<SeatViewDetailResult> resultPage = seatViewPage.map(sv ->
                SeatViewDetailResult.from(
                        sv,
                        emotionTagMap.getOrDefault(sv.getId(), new ArrayList<>()),
                        s3Uploader.generatePresignedGetUrl(sv.getView_media_url())
                )
        );

        return resultPage;
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