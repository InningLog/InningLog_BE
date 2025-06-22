package com.inninglog.inninglog.kbo.dto;

import lombok.Data;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import java.util.List;


//월별 일정 요청 dto
@Data
@NoArgsConstructor
@AllArgsConstructor
public class GameScheduleRequestDto {
    private List<GameScheduleDto> games;
    private String yearMonth;  // "2025-06"
    private String type;       // "SCHEDULE"
}