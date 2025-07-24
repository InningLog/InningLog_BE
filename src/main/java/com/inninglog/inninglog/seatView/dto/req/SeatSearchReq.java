package com.inninglog.inninglog.seatView.dto.req;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatSearchReq {
    private String stadiumShortCode;
    private String zoneShortCode;
    private String section;
    private String seatRow;

    // 열만 입력된 경우 검증
    public boolean isValidRequest() {
        // 열만 입력되고 존이 없는 경우는 불가
        if (seatRow != null && !seatRow.trim().isEmpty() &&
                (zoneShortCode == null || zoneShortCode.trim().isEmpty())) {
            return false;
        }
        return true;
    }

    public static SeatSearchReq from(String stadiumShortCode, String zoneShortCode, String section, String seatRow) {
        return SeatSearchReq.builder()
                .stadiumShortCode(stadiumShortCode)
                .zoneShortCode(zoneShortCode)
                .section(section)
                .seatRow(seatRow)
                .build();
    }
}