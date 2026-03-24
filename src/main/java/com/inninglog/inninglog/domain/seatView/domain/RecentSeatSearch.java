package com.inninglog.inninglog.domain.seatView.domain;

import com.inninglog.inninglog.domain.member.domain.Member;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RecentSeatSearch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id", nullable = false)
    private Member member;

    @Column(nullable = false)
    private String stadiumShortCode;

    @Column(nullable = false)
    private String section;

    private String seatRow;

    @Column(nullable = false)
    private LocalDateTime searchedAt;

    public void updateSearchedAt() {
        this.searchedAt = LocalDateTime.now();
    }
}
