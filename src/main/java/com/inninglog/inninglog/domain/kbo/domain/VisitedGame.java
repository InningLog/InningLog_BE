package com.inninglog.inninglog.domain.kbo.domain;

import com.inninglog.inninglog.global.entity.BaseTimeEntity;
import com.inninglog.inninglog.domain.journal.domain.ResultScore;
import com.inninglog.inninglog.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class VisitedGame extends BaseTimeEntity{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    //경기 결과
    @Enumerated(EnumType.STRING)
    private ResultScore resultScore;
}
