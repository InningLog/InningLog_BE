package com.inninglog.inninglog.journal.dto.res;


import com.inninglog.inninglog.journal.domain.EmotionTag;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.member.domain.Member;
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
public class JourDetailResDto {

    @Schema(description = "직관일지 Id", example = "12")
    private Long journalId;

    @Schema(description = "경기 날짜", example = "2025-06-25T18:30:00")
    private LocalDateTime gameDate;

    @Schema(description = "우리팀 숏코드", example = "OB")
    private String supportTeamSC;

    @Schema(description = "상대팀 숏코드", example = "SS")
    private String opponentTeamSC;

    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumSC;

    @Schema(description = "감정 태그 (감동/짜릿함/답답함/아쉬움/분노 중 하나)", example = "감동")
    private EmotionTag emotion;

    @Schema(description = "경기 결과 이미지 URL (S3 업로드 후 응답받은 링크)", example = "https://s3.amazonaws.com/.../image.jpg", nullable = true)
    private String media_url; // optional

    @Schema(description = "후기글", example = "오늘 경기는 정말 재미있었다!", nullable = true)
    private String review_text; // optional


    public static JourDetailResDto from(Member member, Journal journal) {
        return JourDetailResDto.builder()
                .journalId(journal.getId())
                .gameDate(journal.getDate())
                .supportTeamSC(member.getTeam().getShortCode())
                .opponentTeamSC(journal.getOpponentTeam().getShortCode())
                .stadiumSC(journal.getStadium().getShortCode())
                .emotion(journal.getEmotion())
                .media_url(journal.getMedia_url())
                .review_text(journal.getReview_text())
                .build();
    }
}
