package com.inninglog.inninglog.journal.dto;

import com.inninglog.inninglog.journal.domain.EmotionTag;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.team.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JourCreateReqDto {
    @Schema(description = "게임 Id", example = "20250622OBLG0")
    private String gameId;

    @Schema(description = "경기 날짜 (LocalDateTime 형식)", example = "2025-06-03T18:30:00")
    private LocalDateTime date;

    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumShortCode;

    @Schema(description = "상대팀 숏코드", example = "KIA")
    private String opponentTeamShortCode;

    @Schema(description = "우리팀 점수", example = "3")
    private int ourScore;

    @Schema(description = "상대팀 점수", example = "1")
    private int theirScore;

    @Schema(description = "경기 결과 이미지 URL (S3 업로드 후 응답받은 링크)", example = "https://s3.amazonaws.com/.../image.jpg")
    private String media_url;

    @Schema(description = "감정 태그 (기쁨/슬픔/짜증 중 하나)", example = "기쁨")
    private EmotionTag emotion;

    @Schema(description = "후기글 작성", example = "오늘 경기는 정말 재미있었다!")
    private String review_text;

    @Schema(description = "일지 공개 여부 (true: 공개 / false: 비공개)", example = "true")
    private Boolean is_public;


}