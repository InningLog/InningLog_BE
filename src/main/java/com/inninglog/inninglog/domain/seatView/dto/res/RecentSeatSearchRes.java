package com.inninglog.inninglog.domain.seatView.dto.res;

import com.inninglog.inninglog.domain.seatView.domain.RecentSeatSearch;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RecentSeatSearchRes {

    private String stadiumShortCode;
    private String section;
    private String seatRow;

    public static RecentSeatSearchRes from(RecentSeatSearch entity) {
        return RecentSeatSearchRes.builder()
                .stadiumShortCode(entity.getStadiumShortCode())
                .section(entity.getSection())
                .seatRow(entity.getSeatRow())
                .build();
    }
}
