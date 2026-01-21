package com.inninglog.inninglog.domain.kbo.domain;

import com.inninglog.inninglog.domain.team.domain.Team;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    //선수 이름
    private String name;

    //선수 타입
    @Enumerated(EnumType.STRING)
    private PlayerType playerType;

    //속한 팀
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "team_id")
    private Team team;
}
