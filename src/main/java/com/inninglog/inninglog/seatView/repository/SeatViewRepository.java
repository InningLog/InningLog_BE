package com.inninglog.inninglog.seatView.repository;

import com.inninglog.inninglog.seatView.domain.SeatView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface SeatViewRepository extends JpaRepository<SeatView, Long> {

    @Query("SELECT sv FROM SeatView sv " +
            "JOIN FETCH sv.zone z " +
            "JOIN FETCH sv.stadium s " +
            "WHERE s.shortCode = :stadiumShortCode " +
            "AND (:zoneShortCode IS NULL OR z.shortCode = :zoneShortCode) " +
            "AND (:section IS NULL OR sv.section = :section) " +
            "AND (:seatRow IS NULL OR sv.seatRow = :seatRow)")
    List<SeatView> findSeatViewsBySearchCriteria(
            @Param("stadiumShortCode") String stadiumShortCode,
            @Param("zoneShortCode") String zoneShortCode,
            @Param("section") String section,
            @Param("seatRow") String seatRow
    );

}
