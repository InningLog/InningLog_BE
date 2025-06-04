package com.inninglog.inninglog.journal.dto;

import com.inninglog.inninglog.journal.domain.EmotionTag;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.team.domain.Team;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JourCreateReqDto {

    //경기 날짜

    private LocalDate date;

    //상대팀
    @Schema(description = "상대팀 숏코드 작성", example = "KIA")
    private String opponentTeamShortCode;

    //우리 팀 점수
    @Schema(description = "우리팀 점수", example = "0")
    private int ourScore;

    //상대팀 점수
    @Schema(description = "상대팀 점수", example = "1")
    private int theirScore;

    //경기 결과
    @Schema(description = "경기 결과 승/무승부/패", example = "무승부")
    private ResultScore resultScore;

    //감정 태그
    @Schema(description = "감정 태그 기쁨/슬픔/짜증 중 한개 작성", example = "기쁨")
    private EmotionTag emotion;

    //후기 글
    @Schema(description = "후기글 작성", example = "오늘 너무 재미있었다.")
    private String review_text;

    //공개, 비공개 여부
    @Schema(description = "일지 비공개/공개 여부", example = "0")
    private Boolean is_public;

    //경기장 정보
    @Schema(description = "경기장 숏코드", example = "JAM")
    private String stadiumShortCode;

}
