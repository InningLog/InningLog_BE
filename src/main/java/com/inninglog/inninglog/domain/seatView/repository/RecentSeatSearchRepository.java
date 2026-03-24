package com.inninglog.inninglog.domain.seatView.repository;

import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.seatView.domain.RecentSeatSearch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecentSeatSearchRepository extends JpaRepository<RecentSeatSearch, Long> {

    List<RecentSeatSearch> findByMemberAndStadiumShortCodeOrderBySearchedAtDesc(Member member, String stadiumShortCode);

    Optional<RecentSeatSearch> findByMemberAndStadiumShortCodeAndSectionAndSeatRow(
            Member member, String stadiumShortCode, String section, String seatRow);

    long countByMemberAndStadiumShortCode(Member member, String stadiumShortCode);

    Optional<RecentSeatSearch> findFirstByMemberAndStadiumShortCodeOrderBySearchedAtAsc(
            Member member, String stadiumShortCode);
}
