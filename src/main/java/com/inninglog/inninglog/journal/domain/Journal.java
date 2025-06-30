package com.inninglog.inninglog.journal.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.journal.dto.req.JourCreateReqDto;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.team.domain.Team;
import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
@Setter
public class Journal extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    //경기 날짜
    private LocalDateTime date;

    //상대팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "opponent_team_id")
    private Team opponentTeam;

    //우리 팀 점수
    private int ourScore;

    //상대팀 점수
    private int theirScore;

    //경기 결과
    @Enumerated(EnumType.STRING)
    private ResultScore resultScore;

    //감정 태그
    @Enumerated(EnumType.STRING)
    private EmotionTag emotion;

    //후기 글
    private String review_text;

    //미디어 링크
    private String media_url;

    //경기장 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    //시야 정보
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatView_id")
    private SeatView seatView;

    public static Journal from(JourCreateReqDto dto, Member member, Team team, Stadium stadium) {
        ResultScore resultScore;
        if (dto.getOurScore() > dto.getTheirScore()) {
            resultScore = ResultScore.WIN;
        } else if (dto.getOurScore() < dto.getTheirScore()) {
            resultScore = ResultScore.LOSE;
        } else {
            resultScore = ResultScore.DRAW;
        }

        return Journal.builder()
                .member(member)
                .date(dto.getGameDateTime())
                .opponentTeam(team)
                .stadium(stadium)
                .resultScore(resultScore)
                .ourScore(dto.getOurScore())
                .theirScore(dto.getTheirScore())
                .emotion(dto.getEmotion())
                .review_text(dto.getReview_text())
                .media_url(dto.getMedia_url())
                .build();
    }

}
