package com.inninglog.inninglog.kbo.repository;

import com.inninglog.inninglog.kbo.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    List<Game> findAllByLocalDateTimeBetween(LocalDateTime start, LocalDateTime end);}
