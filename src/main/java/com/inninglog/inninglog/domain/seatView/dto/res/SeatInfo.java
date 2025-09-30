package com.inninglog.inninglog.domain.seatView.dto.res;

import com.inninglog.inninglog.domain.seatView.domain.SeatView;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "좌석의 구역, 구단, 경기장 등의 정보")
public class SeatInfo {

    @Schema(description = "존 이름", example = "익사이팅존")
    private String zoneName;

    @Schema(description = "존 숏코드", example = "JAM_RED")
    private String zoneShortCode;

    @Schema(description = "좌석 구역", example = "13구역")
    private String section;

    @Schema(description = "좌석 열(Row)", example = "3열")
    private String seatRow;

    @Schema(description = "경기장 숏코드", example = "JAM")
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