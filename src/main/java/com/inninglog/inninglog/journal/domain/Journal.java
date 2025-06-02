package com.inninglog.inninglog.journal.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

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
    private LocalDate date;

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

    //공개, 비공개 여부
    private Boolean is_public;

    //경기장 정보
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id")
    private Stadium stadium;

    //시야 정보
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seatView_id")
    private SeatView seatView;


}
