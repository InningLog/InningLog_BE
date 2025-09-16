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
@Schema(description = "조건부 좌석 검색 결과 응답 DTO")
public class SeatSearchRes {

    @Schema(description = "검색 조건 요약 문구", example = "익사이팅존 13구역 3열")
    private String searchSummary;

    @Schema(description = "검색 조건에 해당하는 좌석 정보 리스트")
    private List<SeatViewDetailResult> seatViews;

    @Schema(description = "총 검색 결과 수", example = "12")
    private int totalCount;
}