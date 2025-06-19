package com.inninglog.inninglog.kbo.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PlayerStat {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "game_id")
    private Game game;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player_id")
    private Player player;

    //선수 타입
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    //투수 - 이닝
    private double inning;

    //투수 - 자책
    private int earned;

    //타자 - 안타
    private int hits;

    //타자 - 타수
    private int at_bats;
}
