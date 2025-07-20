package com.inninglog.inninglog.seatView.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
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
import lombok.extern.slf4j.Slf4j;
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
@Slf4j
public class HashtagSearchService {

    private final SeatViewRepository seatViewRepository;
    private final SeatViewEmotionTagMapRepository emotionTagMapRepository;
    private final S3Uploader s3Uploader;

    // 모아보기 형태 검색 (사진만)
    public Page<SeatViewImageResult> searchSeatViewsByHashtagsGallery(String stadiumShortCode, List<String> hashtagCodes, Pageable pageable) {
        validateHashtagRequest(hashtagCodes);

        log.info("🔍 해시태그 갤러리 검색 요청 | stadium={}, hashtags={}, page={}", stadiumShortCode, hashtagCodes, pageable.getPageNumber());

        Page<SeatView> seatViewPage = seatViewRepository.findSeatViewsByHashtagsAndPaged(
                stadiumShortCode,
                hashtagCodes,
                hashtagCodes.size(),
                pageable
        );

        log.info("✅ 갤러리 검색 결과 {}개 반환", seatViewPage.getContent().size());

        return seatViewPage.map(sv -> SeatViewImageResult.builder()
                .seatViewId(sv.getId())
                .viewMediaUrl(s3Uploader.generatePresignedGetUrl(sv.getView_media_url()))
                .build());
    }

    // 게시물 형태 검색 (상세 정보 포함)
    public Page<SeatViewDetailResult> searchSeatViewsByHashtagsDetail(String stadiumShortCode, List<String> hashtagCodes, Pageable pageable) {
        validateHashtagRequest(hashtagCodes);

        log.info("🔍 해시태그 상세 검색 요청 | stadium={}, hashtags={}, page={}", stadiumShortCode, hashtagCodes, pageable.getPageNumber());

        Page<SeatView> seatViewPage = seatViewRepository.findSeatViewsByHashtagsWithDetailsAndPaged(
                stadiumShortCode,
                hashtagCodes,
                hashtagCodes.size(),
                pageable
        );

        List<Long> seatViewIds = seatViewPage.getContent().stream()
                .map(SeatView::getId)
                .toList();

        Map<Long, List<SeatViewEmotionTagDto>> emotionTagMap = getEmotionTagMap(seatViewIds);

        log.info("✅ 상세 검색 결과 {}개 반환", seatViewIds.size());

        return seatViewPage.map(sv ->
                SeatViewDetailResult.from(
                        sv,
                        s3Uploader.generatePresignedGetUrl(sv.getView_media_url())
                )
        );
    }

    private Map<Long, List<SeatViewEmotionTagDto>> getEmotionTagMap(List<Long> seatViewIds) {
        if (seatViewIds.isEmpty()) return Map.of();

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

    private void validateHashtagRequest(List<String> hashtagCodes) {
        if (hashtagCodes == null || hashtagCodes.isEmpty() || hashtagCodes.size() > 5) {
            log.warn("❌ 잘못된 해시태그 요청: {}", hashtagCodes);
            throw new CustomException(ErrorCode.INVALID_HASHTAG_REQUEST);
        }
    }
}