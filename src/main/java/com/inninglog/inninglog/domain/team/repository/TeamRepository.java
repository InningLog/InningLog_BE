package com.inninglog.inninglog.domain.team.repository;

import com.inninglog.inninglog.domain.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TeamRepository extends JpaRepository<Team, Long> {
    Optional<Team> findById(Long id);
    Optional<Team> findByShortCode(String shortCode);

    Optional<Team> findByName(String name);

    /**
     * 팀명 또는 shortCode로 팀 찾기
     */
    @Query("SELECT t FROM Team t WHERE UPPER(t.name) = UPPER(:nameOrCode) OR UPPER(t.shortCode) = UPPER(:nameOrCode)")
    Optional<Team> findByNameOrShortCodeIgnoreCase(String nameOrCode);

}
