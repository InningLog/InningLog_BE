package com.inninglog.inninglog.home.dto;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.gameReport.GameHomeResDto;
import com.inninglog.inninglog.member.domain.Member;
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

    @Schema(description = "유저의 닉네임", example = "구혜승")
    private String nickName;

    @Schema(description = "유저의 응원팀 숏코드", example = "OB")
    private String supportTeamSC;

    @Schema(description = "나의 직관 승률 (단위: 할푼리)", example = "667")
    private int myWeaningRate;

    @Schema(description = "내 응원 팀의 경기 일정 목록", example = "[{...}, {...}]")
    private List<GameHomeResDto> myTeamSchedule;

    public static HomeResDto from(Member member, int myWeaningRate, List<GameHomeResDto> myTeamSchedule) {
        return HomeResDto.builder()
                .nickName(member.getNickname())
                .supportTeamSC(member.getTeam().getShortCode())
                .myWeaningRate(myWeaningRate)
                .myTeamSchedule(myTeamSchedule)
                .build();
    }
}
