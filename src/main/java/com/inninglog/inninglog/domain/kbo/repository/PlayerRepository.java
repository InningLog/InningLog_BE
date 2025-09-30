package com.inninglog.inninglog.domain.kbo.repository;

import com.inninglog.inninglog.domain.kbo.domain.Player;
import com.inninglog.inninglog.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<Player, Long> {
    Optional<Player> findByNameAndTeam(String name, Team team);
    Optional<Player> findByName(String name);
}