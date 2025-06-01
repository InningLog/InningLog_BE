package com.inninglog.inninglog.team.repository;

import com.inninglog.inninglog.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findByShortCode(String shortCode);
}
