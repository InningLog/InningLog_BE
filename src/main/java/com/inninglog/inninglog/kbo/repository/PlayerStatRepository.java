package com.inninglog.inninglog.kbo.repository;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.domain.Player;
import com.inninglog.inninglog.kbo.domain.PlayerStat;
import com.inninglog.inninglog.kbo.domain.PlayerType;
import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.team.domain.Team;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface PlayerStatRepository extends JpaRepository<PlayerStat, Long> {
    boolean existsByGameAndPlayerAndPlayerType(Game game, Player player, PlayerType playerType);
    List<PlayerStat> findByGame(Game game);
    List<PlayerStat> findByPlayer(Player player);
    List<PlayerStat> findByGameAndPlayerType(Game game, PlayerType playerType);


    //유저가 직관한 경기들 중, 유저의 응원팀 소속 선수들의 경기 기록만 조회하는 쿼리
    @Query("SELECT ps FROM PlayerStat ps " +
            "JOIN ps.player p " +
            "WHERE ps.game.id IN :gameIds AND p.team = :team")
    List<PlayerStat> findByGameIdsAndTeam(@Param("gameIds") Set<Long> gameIds,
                                          @Param("team") Team team);
}