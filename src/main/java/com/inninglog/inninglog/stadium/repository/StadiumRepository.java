package com.inninglog.inninglog.stadium.repository;

import com.inninglog.inninglog.stadium.domain.Stadium;
import org.springframework.data.jpa.repository.JpaRepository;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {
}
