package com.inninglog.inninglog.kbo.repository;

import com.inninglog.inninglog.kbo.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
}
