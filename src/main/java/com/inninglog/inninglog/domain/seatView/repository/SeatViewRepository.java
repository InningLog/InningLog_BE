package com.inninglog.inninglog.domain.seatView.repository;

import com.inninglog.inninglog.domain.seatView.domain.SeatView;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import org.springframework.data.domain.Pageable;
import java.util.List;

public interface SeatViewRepository extends JpaRepository<SeatView, Long> {


    @Query(
            value = "SELECT sv FROM SeatView sv " +
                    "JOIN FETCH sv.zone z " +
                    "JOIN FETCH sv.stadium s " +
                    "WHERE s.shortCode = :stadiumShortCode " +
                    "AND (SELECT COUNT(DISTINCT setm.seatViewEmotionTag.code) " +
                    "     FROM SeatViewEmotionTagMap setm " +
                    "     WHERE setm.seatView.id = sv.id " +
                    "     AND setm.seatViewEmotionTag.code IN :hashtagCodes) = :hashtagCount",
            countQuery = "SELECT COUNT(sv) FROM SeatView sv " +
                    "JOIN sv.zone z " +
                    "JOIN sv.stadium s " +
                    "WHERE s.shortCode = :stadiumShortCode " +
                    "AND (SELECT COUNT(DISTINCT setm.seatViewEmotionTag.code) " +
                    "     FROM SeatViewEmotionTagMap setm " +
                    "     WHERE setm.seatView.id = sv.id " +
                    "     AND setm.seatViewEmotionTag.code IN :hashtagCodes) = :hashtagCount"
    )
    Page<SeatView> findSeatViewsByHashtagsWithDetailsAndPaged(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("hashtagCodes") List<String> hashtagCodes,
            @Param("hashtagCount") long hashtagCount,
            Pageable pageable
    );

    @Query(value = """
        SELECT sv FROM SeatView sv
        JOIN sv.stadium s
        WHERE s.shortCode = :stadiumShortCode
        AND (
            SELECT COUNT(DISTINCT setm.seatViewEmotionTag.code)
            FROM SeatViewEmotionTagMap setm
            WHERE setm.seatView.id = sv.id
            AND setm.seatViewEmotionTag.code IN :hashtagCodes
        ) = :hashtagCount
        """,
            countQuery = """
        SELECT COUNT(sv) FROM SeatView sv
        JOIN sv.stadium s
        WHERE s.shortCode = :stadiumShortCode
        AND (
            SELECT COUNT(DISTINCT setm.seatViewEmotionTag.code)
            FROM SeatViewEmotionTagMap setm
            WHERE setm.seatView.id = sv.id
            AND setm.seatViewEmotionTag.code IN :hashtagCodes
        ) = :hashtagCount
        """)
    Page<SeatView> findSeatViewsByHashtagsAndPaged(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("hashtagCodes") List<String> hashtagCodes,
            @Param("hashtagCount") long hashtagCount,
            Pageable pageable
    );

    @Query("SELECT sv FROM SeatView sv " +
            "JOIN FETCH sv.zone z " +
            "JOIN FETCH sv.stadium s " +
            "WHERE s.shortCode = :stadiumShortCode " +
            "AND (:zoneShortCode IS NULL OR z.shortCode = :zoneShortCode) " +
            "AND (:section IS NULL OR sv.section = :section) " +
            "AND (:seatRow IS NULL OR sv.seatRow = :seatRow)")
    Page<SeatView> findSeatViewsBySearchCriteriaPageable(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("zoneShortCode") String zoneShortCode,
            @Param("section") String section,
            @Param("seatRow") String seatRow,
            Pageable pageable
    );

}
