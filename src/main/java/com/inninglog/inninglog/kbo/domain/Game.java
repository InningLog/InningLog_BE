package com.inninglog.inninglog.kbo.domain;

import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.stadium.domain.Stadium;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "game",
        uniqueConstraints = @UniqueConstraint(name = "uk_game_id", columnNames = "game_id"))
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class Game {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "game_id", nullable = false, unique = true, length = 20)
    private String gameId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "away_team_id", nullable = false)
    private Team awayTeam;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "home_team_id", nullable = false)
    private Team homeTeam;

    @Column(name = "away_score", nullable = false)
    private Integer awayScore;

    @Column(name = "home_score", nullable = false)
    private Integer homeScore;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "stadium_id", nullable = false)
    private Stadium stadium;

    @Column(name = "local_date_time", nullable = false)
    private LocalDateTime localDateTime;

    @Column(name = "local_date", nullable = false)
    private LocalDate localDate;

    @Column(name = "boxscore_url", length = 500)
    private String boxscoreUrl;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private GameStatus status = GameStatus.SCHEDULED;

    @Column(name = "created_at", nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    /**
     * 경기 결과 업데이트
     */
    public void updateResult(Integer awayScore, Integer homeScore, String boxscoreUrl, GameStatus status) {
        this.awayScore = awayScore;
        this.homeScore = homeScore;
        this.boxscoreUrl = boxscoreUrl;
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 박스스코어 URL 업데이트
     */
    public void updateBoxscoreUrl(String boxscoreUrl) {
        this.boxscoreUrl = boxscoreUrl;
        this.updatedAt = LocalDateTime.now();
    }

    /**
     * 상태 업데이트
     */
    public void updateStatus(GameStatus status) {
        this.status = status;
        this.updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }

    @PrePersist
    protected void onCreate() {
        // localDate가 설정되지 않았으면 localDateTime에서 추출
        if (this.localDate == null && this.localDateTime != null) {
            this.localDate = this.localDateTime.toLocalDate();
        }
    }
}