package com.inninglog.inninglog.seatView.dto;


import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTag;
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
    private Long journalId;

    //좌석 정보 원본
    private String seatInfo;

    //경기장 숏코드
    private String stadiumShortCode;

    // 감정 태그 코드 리스트만 받음
    private List<String> emotionTagCodes;

}
