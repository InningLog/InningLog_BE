package com.inninglog.inninglog.journal.dto;

import com.inninglog.inninglog.journal.domain.EmotionTag;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.team.domain.Team;
import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDate;

@Data
public class JourCreateReqDto {

    //경기 날짜
    private LocalDate date;

    //상대팀
    private String opponentTeamShortCode;

    //우리 팀 점수
    private int ourScore;

    //상대팀 점수
    private int theirScore;

    //경기 결과
    private ResultScore resultScore;

    //감정 태그
    private EmotionTag emotion;

    //후기 글
    private String review_text;

    //공개, 비공개 여부
    private Boolean is_public;

    //경기장 정보
    private String stadiumShortCode;

}
