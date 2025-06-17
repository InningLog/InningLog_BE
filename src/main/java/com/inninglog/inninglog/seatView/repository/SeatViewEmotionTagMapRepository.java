package com.inninglog.inninglog.seatView.repository;

import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTagMap;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatViewEmotionTagMapRepository extends JpaRepository<SeatViewEmotionTagMap, Long>{
}
