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
    private String section;
    private String seatRow;

    // 검증: 경기장 필수, section 필수, seatRow만 단독 불가
    public boolean isValidRequest() {
        boolean hasStadium = stadiumShortCode != null && !stadiumShortCode.trim().isEmpty();
        boolean hasSection = section != null && !section.trim().isEmpty();
        boolean hasRow = seatRow != null && !seatRow.trim().isEmpty();

        // 1. 경기장은 필수
        if (!hasStadium) {
            return false;
        }

        // 2. seatRow가 있으면 section도 있어야 함
        if (hasRow && !hasSection) {
            return false;
        }

        // 3. stadium만 있는 상태 (section, seatRow 모두 null)도 불가
        if (!hasSection && !hasRow) {
            return false;
        }

        return true;
    }

    public static SeatSearchReq from(String stadiumShortCode, String section, String seatRow) {
        return SeatSearchReq.builder()
                .stadiumShortCode(stadiumShortCode)
                .section(section)
                .seatRow(seatRow)
                .build();
    }
}