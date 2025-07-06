package com.inninglog.inninglog.seatView.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatViewDetailResult {
    private Long seatViewId;
    private String viewMediaUrl;
    private SeatInfo seatInfo;
    private List<SeatViewEmotionTagDto> emotionTags;
}