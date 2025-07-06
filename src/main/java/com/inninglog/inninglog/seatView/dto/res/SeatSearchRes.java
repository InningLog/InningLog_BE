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
public class SeatSearchRes {
    private String searchSummary;
    private List<SeatViewDetailResult> seatViews;
    private int totalCount;
}
