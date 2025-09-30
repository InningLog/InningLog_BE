package com.inninglog.inninglog.domain.seatView.dto.req;

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
        boolean hasStadium = stadiumShortCode != null && !stadiumShortCode.trim().isEmpty();
        boolean hasRow = seatRow != null && !seatRow.trim().isEmpty();
        boolean hasZone = zoneShortCode != null && !zoneShortCode.trim().isEmpty();
        boolean hasSection = section != null && !section.trim().isEmpty();

        // 1. 경기장은 필수
        if (!hasStadium) {
            return false;
        }

        // 2. seatRow가 있으면, 최소한 zone이나 section 하나는 있어야 함
        if (hasRow && !hasZone && !hasSection) {
            return false;
        }

        // 3. stadium만 있는 상태 (나머지 전부 null)도 ❌
        if (!hasZone && !hasSection && !hasRow) {
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