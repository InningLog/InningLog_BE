package com.inninglog.inninglog.domain.stadium.repository;

import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {
    Optional<Stadium> findByShortCode(String shortCode);

    Optional<Stadium> findByName(String name);
}
