package com.inninglog.inninglog.kbo.domain;

import com.inninglog.inninglog.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class MemberPlayerStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "memeber_id")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    //투수 - 이닝
    private double total_innings;

    //투수 - 자책
    private int total_earned;

    //타자 - 안타
    private int total_hits;

    //타자 - 타수
    private int total_at_bats;
}
