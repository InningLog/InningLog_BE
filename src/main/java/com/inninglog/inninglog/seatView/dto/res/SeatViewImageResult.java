package com.inninglog.inninglog.seatView.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 모아보기용 - 사진만
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatViewImageResult {
    private Long seatViewId;
    private String viewMediaUrl;
}