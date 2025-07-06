package com.inninglog.inninglog.seatView.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HashtagSearchRes {
    private String searchSummary;
    private List<SeatViewImageResult> seatViews;
    private int totalCount;
    private boolean isGalleryView; // 모아보기 형태인지 구분
}