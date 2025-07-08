package com.inninglog.inninglog.seatView.dto.res;

import com.inninglog.inninglog.seatView.domain.SeatView;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatInfo {
    private String zoneName;
    private String zoneShortCode;
    private String section;
    private String seatRow;
    private String stadiumName;

    public static SeatInfo from(SeatView seatView) {
        return SeatInfo.builder()
                .zoneName(seatView.getZone().getName())
                .zoneShortCode(seatView.getZone().getShortCode())
                .section(seatView.getSection())
                .seatRow(seatView.getSeatRow())
                .stadiumName(seatView.getStadium().getName())
                .build();
    }
}