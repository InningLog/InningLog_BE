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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SeatViewService {

    private final MemberRepository memberRepository;
    private final JournalRepository journalRepository;
    private final StadiumRepository stadiumRepository;
    private final ZoneRepository zoneRepository;

    private final SeatViewRepository seatViewRepository;

    private final SeatViewEmotionTagRepository seatViewEmotionTagRepository;
    private final SeatViewEmotionTagMapRepository seatViewEmotionTagMapRepository;
    private final S3Uploader s3Uploader;

    //좌석 시야 사진 업로드
    @Transactional
    public String UploadImage(MultipartFile file) {

        String media_url = null;
        if (file != null && !file.isEmpty()) {
            try {
                media_url = s3Uploader.uploadFile(file, "seatView");
            } catch (IOException e) {
                throw new RuntimeException("S3 업로드 실패", e);
            }
        }

        //S3에 업로드한 url 반환
        return media_url;
    }



    //좌석 시야 정보 작성
    @Transactional
    public SeatCreateResDto createSeatView(Long memberId, SeatCreateReqDto dto) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        Journal journal = journalRepository.findById(dto.getJournalId())
                .orElseThrow(() -> new CustomException(ErrorCode.JOURNAL_NOT_FOUND));

        Stadium stadium = stadiumRepository.findByShortCode(dto.getStadiumShortCode())
                .orElseThrow(() -> new CustomException(ErrorCode.STADIUM_NOT_FOUND));

        Zone zone = zoneRepository.findByShortCode(dto.getZoneShortCode())
                .orElseThrow(()-> new CustomException(ErrorCode.ZONE_NOT_FOUND));

        SeatView seatView = SeatView.builder()
                .member(member)
                .journal(journal)
                .stadium(stadium)
                .view_media_url(dto.getMedia_url())
                .zone(zone)
                .section(dto.getSection())
                .seatRow(dto.getSeatRow())
                .build();

        journal.setSeatView(seatView);
        seatViewRepository.save(seatView);


        //좌석 관련 감정 태그 생성
            for(String code : dto.getEmotionTagCodes()) {
                SeatViewEmotionTag seatViewemotionTag = seatViewEmotionTagRepository.findByCode(code)
                        .orElseThrow(() -> new CustomException(ErrorCode.EMOTION_TAG_NOT_FOUND));

                SeatViewEmotionTagMap map = SeatViewEmotionTagMap.builder()
                        .seatView(seatView)
                        .seatViewEmotionTag(seatViewemotionTag).build();

                seatViewEmotionTagMapRepository.save(map);
            }

        return SeatCreateResDto.from(seatView);
    }


    //특정 좌석 시야 조회
    public SeatViewDetailResult getSeatView(Long memberId, Long seatViewId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        SeatView seatView = seatViewRepository.findById(seatViewId)
                .orElseThrow(() -> new CustomException(ErrorCode.SEATVIEW_NOT_FOUND));

        // 감정 태그 매핑 조회
        List<SeatViewEmotionTagDto> tags = seatViewEmotionTagMapRepository.findBySeatViewId(seatViewId).stream()
                .map(tagMap -> SeatViewEmotionTagDto.builder()
                        .code(tagMap.getSeatViewEmotionTag().getCode())
                        .label(tagMap.getSeatViewEmotionTag().getLabel())
                        .build())
                .collect(Collectors.toList());

        // 결과 생성
        return SeatViewDetailResult.from(seatView, tags);
    }


}
