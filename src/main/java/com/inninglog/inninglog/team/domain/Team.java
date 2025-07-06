package com.inninglog.inninglog.team.domain;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor //모든 필드를 매개변수로 받는 생성자 생성
@NoArgsConstructor//매개변수가 없는 기본 생성자를 자동으로 생성
@Getter
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    @Column(name = "short_code", unique = true)
    private String shortCode;

    @Column(name = "win_rate", precision = 5)
    private Double winRate;

    @Column(name = "win_rate_updated_at")
    private LocalDateTime winRateUpdatedAt;

    // 승률 업데이트 메서드
    public void updateWinRate(Double winRate) {
        this.winRate = winRate;
        this.winRateUpdatedAt = LocalDateTime.now();
    }

    // 승률 초기화 메서드
    public void clearWinRate() {
        this.winRate = null;
        this.winRateUpdatedAt = null;
    }
}