package com.inninglog.inninglog.seatView.repository;

import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatViewEmotionTagRepository extends JpaRepository<SeatViewEmotionTag, Long> {
Optional<SeatViewEmotionTag> findByCode(String code);
}