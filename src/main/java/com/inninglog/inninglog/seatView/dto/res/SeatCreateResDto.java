package com.inninglog.inninglog.seatView.dto.res;

import com.inninglog.inninglog.seatView.domain.SeatView;
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

    public static SeatCreateResDto from(SeatView seatView) {
        return SeatCreateResDto.builder()
                .JournalId(seatView.getJournal().getId())
                .SeatViewId(seatView.getId())
                .build();
    }
}
