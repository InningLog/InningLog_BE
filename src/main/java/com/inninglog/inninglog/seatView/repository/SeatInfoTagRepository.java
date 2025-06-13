package com.inninglog.inninglog.seatView.repository;

import com.inninglog.inninglog.seatView.domain.SeatInfoTag;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SeatInfoTagRepository extends JpaRepository<SeatInfoTag, Long> {
    Optional<SeatInfoTag> findByTag(String tag);
}
