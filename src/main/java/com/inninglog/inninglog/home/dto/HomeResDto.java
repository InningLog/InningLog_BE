package com.inninglog.inninglog.home.dto;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.gameReport.GameHomeResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

import io.swagger.v3.oas.annotations.media.Schema;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HomeResDto {

    @Schema(description = "나의 직관 승률 (단위: 할푼리)", example = "667")
    private int myWeaningRate;

    @Schema(description = "내 응원 팀의 경기 일정 목록", example = "[{...}, {...}]")
    private List<GameHomeResDto> myTeamSchedule;

    public static HomeResDto from(int myWeaningRate, List<GameHomeResDto> myTeamSchedule) {
        return HomeResDto.builder()
                .myWeaningRate(myWeaningRate)
                .myTeamSchedule(myTeamSchedule)
                .build();
    }
}
