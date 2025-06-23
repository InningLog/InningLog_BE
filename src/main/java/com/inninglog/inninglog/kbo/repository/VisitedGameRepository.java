package com.inninglog.inninglog.kbo.repository;

import com.inninglog.inninglog.kbo.domain.VisitedGame;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VisitedGameRepository extends JpaRepository<VisitedGame, Long> {

}
