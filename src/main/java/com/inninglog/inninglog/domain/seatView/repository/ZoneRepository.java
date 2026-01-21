package com.inninglog.inninglog.domain.seatView.repository;

import com.inninglog.inninglog.domain.seatView.domain.Zone;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ZoneRepository extends JpaRepository<Zone, Long> {
    Optional<Zone> findByShortCode(String shortCode);
}
