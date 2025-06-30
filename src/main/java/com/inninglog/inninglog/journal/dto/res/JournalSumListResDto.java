package com.inninglog.inninglog.journal.dto.res;

import com.inninglog.inninglog.journal.domain.EmotionTag;
import com.inninglog.inninglog.journal.domain.Journal;
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
    private EmotionTag emotion;
    private LocalDateTime date;
    private String opponentTeamName;
    private String stadiumName;

    public static JournalSumListResDto from(Journal journal) {
        return new JournalSumListResDto(
                journal.getId(),
                journal.getMedia_url(),
                journal.getResultScore(),
                journal.getEmotion(),
                journal.getDate(),
                journal.getOpponentTeam().getName(),
                journal.getStadium().getName()
        );
    }

}

