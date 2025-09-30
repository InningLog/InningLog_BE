package com.inninglog.inninglog.domain.seatView.repository;

import com.inninglog.inninglog.domain.seatView.domain.SeatViewEmotionTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatViewEmotionTagMapRepository extends JpaRepository<SeatViewEmotionTagMap, Long>{

    @Query("SELECT setm FROM SeatViewEmotionTagMap setm " +
            "JOIN FETCH setm.seatViewEmotionTag " +
            "WHERE setm.seatView.id IN :seatViewIds")
    List<SeatViewEmotionTagMap> findBySeatViewIds(@Param("seatViewIds") List<Long> seatViewIds);


    @Query("SELECT setm FROM SeatViewEmotionTagMap setm " +
            "JOIN FETCH setm.seatViewEmotionTag " +
            "WHERE setm.seatView.id = :seatViewId")
    List<SeatViewEmotionTagMap> findBySeatViewId(@Param("seatViewId") Long seatViewId);
}
