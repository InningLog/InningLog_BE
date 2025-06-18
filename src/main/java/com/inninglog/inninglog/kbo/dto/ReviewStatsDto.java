package com.inninglog.inninglog.kbo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.ArrayList;
import java.util.List;

@Data
public class ReviewStatsDto {
    private List<PitcherStatDto> pitchers = new ArrayList<>();
    private List<HitterStatDto>  batters  = new ArrayList<>();
}