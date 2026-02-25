package com.inninglog.inninglog.domain.seatView.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.global.s3.S3Uploader;
import com.inninglog.inninglog.domain.member.repository.MemberRepository;
import com.inninglog.inninglog.domain.seatView.domain.SeatView;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatViewImageResult;
import com.inninglog.inninglog.domain.seatView.repository.SeatViewEmotionTagMapRepository;
import com.inninglog.inninglog.domain.seatView.repository.SeatViewRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class HashtagSearchService {

    private final SeatViewRepository seatViewRepository;
    private final SeatViewEmotionTagMapRepository emotionTagMapRepository;
    private final S3Uploader s3Uploader;
    private final MemberRepository memberRepository;

    // 모아보기 형태 검색 (사진만)
    public Page<SeatViewImageResult> searchSeatViewsByHashtagsGallery(Long memberId, String stadiumShortCode, List<String> hashtagCodes, Pageable pageable) {
        validateHashtagRequest(hashtagCodes);

        memberRepository.findById(memberId)
                        .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

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
                .viewMediaUrl(s3Uploader.getDirectUrl(sv.getView_media_url()))
                .build());
    }



    private void validateHashtagRequest(List<String> hashtagCodes) {
        if (hashtagCodes == null || hashtagCodes.isEmpty() || hashtagCodes.size() > 5) {
            log.info("📌 [validateHashtagRequest] hashtagCodes={} 잘못된 해시태그 요청", hashtagCodes);
            throw new CustomException(ErrorCode.INVALID_HASHTAG_REQUEST);
        }
    }
}