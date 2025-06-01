package com.inninglog.inninglog.team.repository;

import com.inninglog.inninglog.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TeamRepository extends JpaRepository<Team, Long> {
}
