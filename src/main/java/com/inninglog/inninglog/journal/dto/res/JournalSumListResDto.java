package com.inninglog.inninglog.journal.dto.res;


import com.inninglog.inninglog.journal.domain.EmotionTag;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
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
public class JournalSumListResDto {

    @Schema(description = "일지 고유 ID", example = "12")
    private Long journalId;

    @Schema(description = "일지에 첨부된 미디어 파일 S3 URL", example = "https://s3.ap-northeast-2.amazonaws.com/.../image.jpg")
    private String media_url;

    @Schema(description = "경기 결과 (WIN, LOSE, DRAW)", example = "WIN")
    private ResultScore resultScore;

    @Schema(description = "기록 시 감정 태그 (감동, 짜릿함, ...)", example = "감동")
    private EmotionTag emotion;

    @Schema(description = "경기 날짜 및 시간", example = "2025-07-09 18:30")
    private String gameDate;

    @Schema(description = "우리팀 숏코드", example = "OB")
    private String supportTeamSC;

    @Schema(description = "경기 상대 팀 숏코드", example = "SS")
    private String opponentTeamSC;

    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumSC;

    public static JournalSumListResDto from(Journal journal, String presignedUrl, String supportTeamSC) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        String formattedDate = journal.getDate().format(formatter);


        return new JournalSumListResDto(
                journal.getId(),
                presignedUrl,
                journal.getResultScore(),
                journal.getEmotion(),
                formattedDate,
                supportTeamSC,
                journal.getOpponentTeam().getShortCode(),
                journal.getStadium().getShortCode()
        );
    }
}