package com.inninglog.inninglog.kbo.repository;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.domain.Player;
import com.inninglog.inninglog.kbo.domain.PlayerStat;
import com.inninglog.inninglog.kbo.domain.PlayerType;
import com.inninglog.inninglog.kbo.domain.Game;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {
    boolean existsByGameAndPlayerAndPlayerType(Game game, Player player, PlayerType playerType);
    List<PlayerStat> findByGame(Game game);
    List<PlayerStat> findByPlayer(Player player);
    List<PlayerStat> findByGameAndPlayerType(Game game, PlayerType playerType);
}