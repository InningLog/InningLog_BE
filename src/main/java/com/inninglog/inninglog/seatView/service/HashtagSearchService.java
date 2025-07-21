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

        log.info("📌 [searchSeatViewsByHashtagsGallery] stadiumShortCode='{}', hashtagCodes={}, page={} 해시태그 갤러리 검색 요청",
                stadiumShortCode, hashtagCodes, pageable.getPageNumber());

        Page<SeatView> seatViewPage = seatViewRepository.findSeatViewsByHashtagsAndPaged(
                stadiumShortCode,
                hashtagCodes,
                hashtagCodes.size(),
                pageable
        );

        log.info("📌 [searchSeatViewsByHashtagsGallery] stadiumShortCode='{}' 갤러리 검색 결과: resultCount={}",
                stadiumShortCode, seatViewPage.getContent().size());

        return seatViewPage.map(sv -> SeatViewImageResult.builder()
                .seatViewId(sv.getId())
                .viewMediaUrl(s3Uploader.generatePresignedGetUrl(sv.getView_media_url()))
                .build());
    }

    // 게시물 형태 검색 (상세 정보 포함)
    public Page<SeatViewDetailResult> searchSeatViewsByHashtagsDetail(String stadiumShortCode, List<String> hashtagCodes, Pageable pageable) {
        validateHashtagRequest(hashtagCodes);

        log.info("📌 [searchSeatViewsByHashtagsDetail] stadiumShortCode='{}', hashtagCodes={}, page={} 해시태그 상세 검색 요청",
                stadiumShortCode, hashtagCodes, pageable.getPageNumber());

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

        log.info("📌 [searchSeatViewsByHashtagsDetail] stadiumShortCode='{}' 상세 검색 결과: resultCount={}",
                stadiumShortCode, seatViewIds.size());

        return seatViewPage.map(sv ->
                SeatViewDetailResult.from(
                        sv,
                        s3Uploader.generatePresignedGetUrl(sv.getView_media_url())
                )
        );
    }

    private Map<Long, List<SeatViewEmotionTagDto>> getEmotionTagMap(List<Long> seatViewIds) {
        if (seatViewIds.isEmpty()) {
            log.info("📌 [getEmotionTagMap] seatViewIds가 비어있음");
            return Map.of();
        }

        List<SeatViewEmotionTagMap> tagMaps = emotionTagMapRepository.findBySeatViewIds(seatViewIds);

        log.info("📌 [getEmotionTagMap] seatViewIds.size={} 감정 태그 매핑 조회 완료: tagMaps.size={}",
                seatViewIds.size(), tagMaps.size());

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
            log.info("📌 [validateHashtagRequest] hashtagCodes={} 잘못된 해시태그 요청", hashtagCodes);
            throw new CustomException(ErrorCode.INVALID_HASHTAG_REQUEST);
        }
    }
}