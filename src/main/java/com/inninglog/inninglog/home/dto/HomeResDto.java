package com.inninglog.inninglog.home.dto;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.gameReport.GameHomeResDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class HomeResDto {

    private int myWeaningRate;
    private List<GameHomeResDto> myTeamSchedule;


    public static HomeResDto from(int myWeaningRate, List<GameHomeResDto> myTeamSchedule) {
        return HomeResDto.builder()
                .myWeaningRate(myWeaningRate)
                .myTeamSchedule(myTeamSchedule)
                .build();
    }
}
