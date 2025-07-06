package com.inninglog.inninglog.seatView.dto;

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
}