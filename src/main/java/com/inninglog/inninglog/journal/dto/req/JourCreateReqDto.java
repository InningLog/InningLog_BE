package com.inninglog.inninglog.journal.dto.req;

import com.inninglog.inninglog.journal.domain.EmotionTag;
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
public class JourCreateReqDto {
    @Schema(description = "게임 Id", example = "20250622OBLG0")
    private String gameId;

    @Schema(description = "경기 날짜 (LocalDateTime 형식)", example = "2025-06-03T18:30:00")
    private LocalDateTime gameDateTime;

    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumShortCode;

    @Schema(description = "상대팀 숏코드", example = "OB")
    private String opponentTeamShortCode;

    @Schema(description = "우리팀 점수", example = "3")
    private int ourScore;

    @Schema(description = "상대팀 점수", example = "1")
    private int theirScore;

    @Schema(description = "경기 결과 이미지 URL (S3 업로드 후 응답받은 링크)", example = "https://s3.amazonaws.com/.../image.jpg")
    private String media_url;

    @Schema(description = "감정 태그 (감동/짜릿함/답답함/아쉬움/분노 중 하나)", example = "감동")
    private EmotionTag emotion;

    @Schema(description = "후기글 작성", example = "오늘 경기는 정말 재미있었다!")
    private String review_text;


}