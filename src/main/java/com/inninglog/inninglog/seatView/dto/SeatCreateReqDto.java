package com.inninglog.inninglog.seatView.dto;


import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatCreateReqDto {

    //직관 일지 id
    @Schema(description = "매핑 되는 직관 일지의 id")
    private Long journalId;


    //경기장 숏코드
    private String stadiumShortCode;

    //존 숏코드
    private String zoneShortCode;

    //구역
    private String Section;

    //열
    private String seatRow;

    // 감정 태그 코드 리스트만 받음
    private List<String> emotionTagCodes;

    //이미지 업로드 URl
    private String media_url;

}
