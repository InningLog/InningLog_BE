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
public class JournalSumListResDto {
    private Long journalId;
    private String media_url;
    private ResultScore resultScore;
    private LocalDateTime date;
    private String opponentTeamName;
    private String stadiumName;
}
