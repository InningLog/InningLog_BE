package com.inninglog.inninglog.seatView.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeatCreateResDto {

    private Long SeatViewId;
    private Long JournalId;
}
