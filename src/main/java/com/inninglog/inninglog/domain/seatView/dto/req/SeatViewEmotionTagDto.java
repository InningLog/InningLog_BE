package com.inninglog.inninglog.domain.seatView.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatViewEmotionTagDto {
    private String code;
    private String label;
}
