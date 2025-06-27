package com.inninglog.inninglog.journal.dto.res;

import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "직관일지 Id", example = "12")
    private Long journalId;

    @Schema(description = "우리팀 점수", example = "3")
    private int ourScore;

    @Schema(description = "상대팀 점수", example = "4")
    private int theirScore;

    @Schema(description = "경기 결과 승/무/패 중 하나", example = "WIN")
    private ResultScore resultScore;
    private LocalDateTime date;
    private String opponentTeamName;
    private String stadiumName;

    public static JournalCalListResDto from(Journal journal) {
        return new JournalCalListResDto(
                journal.getId(),
                journal.getOurScore(),
                journal.getTheirScore(),
                journal.getResultScore(),
                journal.getDate(),
                journal.getOpponentTeam().getName(),
                journal.getStadium().getName()
        );
    }
}
