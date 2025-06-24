package com.inninglog.inninglog.kbo.dto.gameReport;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class GameReportResDto {

    //나의 직관 횟수
    private int visited;

    //나의 직관 승리 획수
    private int win;

    //나의 승률
    private int winningRate;

}
