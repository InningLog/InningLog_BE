package com.inninglog.inninglog.domain.journal.dto.res;

import com.inninglog.inninglog.domain.journal.domain.Journal;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;

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

    @Schema(description = "경기 날짜 및 시간", example = "2025-07-09 18:30")
    private String gameDate;

    @Schema(description = "우리팀 숏코드", example = "OB")
    private String supportTeamSC;

    @Schema(description = "경기 상대 팀 이름", example = "SS")
    private String opponentTeamSC;

    @Schema(description = "경기장 이름", example = "JAM")
    private String stadiumSC;

    public static JournalCalListResDto from(Journal journal) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = journal.getDate().format(formatter);

        return new JournalCalListResDto(
                journal.getId(),
                journal.getOurScore(),
                journal.getTheirScore(),
                journal.getResultScore(),
                formattedDate,
                journal.getMember().getTeam().getShortCode(),
                journal.getOpponentTeam().getShortCode(),
                journal.getStadium().getShortCode()
        );
    }
}
