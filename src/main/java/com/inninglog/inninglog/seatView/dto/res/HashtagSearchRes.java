package com.inninglog.inninglog.seatView.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "해시태그 기반 좌석 검색 응답 DTO")
public class HashtagSearchRes {

    @Schema(description = "검색 조건 요약 문구", example = "익사이팅존 + 시야확보")
    private String searchSummary;

    @Schema(description = "해당 조건을 만족하는 좌석 이미지 리스트")
    private List<SeatViewImageResult> seatViews;

    @Schema(description = "총 검색 결과 수", example = "24")
    private int totalCount;

    @Schema(description = "모아보기 형태로 조회 중인지 여부", example = "true")
    private boolean isGalleryView;
}