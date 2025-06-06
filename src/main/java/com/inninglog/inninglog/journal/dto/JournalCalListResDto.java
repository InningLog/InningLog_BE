package com.inninglog.inninglog.journal.dto;

import com.inninglog.inninglog.journal.domain.ResultScore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JournalCalListResDto {

    private Long journalId;
    private int ourScore;
    private int theirScore;
    private ResultScore resultScore;
    private LocalDateTime date;
    private String opponentTeamName;
    private String stadiumName;
}
