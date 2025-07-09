package com.inninglog.inninglog.journal.dto.res;


import com.inninglog.inninglog.journal.domain.EmotionTag;
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
public class JournalSumListResDto {

    @Schema(description = "일지 고유 ID", example = "12")
    private Long journalId;

    @Schema(description = "일지에 첨부된 미디어 파일 S3 URL", example = "https://s3.ap-northeast-2.amazonaws.com/.../image.jpg")
    private String media_url;

    @Schema(description = "경기 결과 (WIN, LOSE, DRAW)", example = "WIN")
    private ResultScore resultScore;

    @Schema(description = "기록 시 감정 태그 (감동, 짜릿함, ...)", example = "감동")
    private EmotionTag emotion;

    @Schema(description = "경기 날짜 및 시간", example = "2025-07-09T18:30:00")
    private LocalDateTime date;

    @Schema(description = "경기 상대 팀 이름", example = "SSG 랜더스")
    private String opponentTeamName;

    @Schema(description = "경기장 이름", example = "잠실")
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