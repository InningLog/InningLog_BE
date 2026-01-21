package com.inninglog.inninglog.domain.journal.dto.res;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourUpdateResDto {

    private JourDetailResDto jourDetail;
    private Long seatViewId;

    public static JourUpdateResDto from (JourDetailResDto jourDetail, Long seatViewId) {
        return JourUpdateResDto.builder().jourDetail(jourDetail).seatViewId(seatViewId).build();

    }
}
