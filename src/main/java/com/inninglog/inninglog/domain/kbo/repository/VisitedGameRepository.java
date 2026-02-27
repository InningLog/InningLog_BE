package com.inninglog.inninglog.domain.kbo.repository;

import com.inninglog.inninglog.domain.kbo.domain.VisitedGame;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.WinningRateProjection;
import com.inninglog.inninglog.domain.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface VisitedGameRepository extends JpaRepository<VisitedGame, Long> {

    List<VisitedGame> findByMember(Member member);

    @Query("SELECT COUNT(vg) AS totalGames, " +
            "SUM(CASE WHEN vg.resultScore = 'WIN' THEN 1 ELSE 0 END) AS winGames, " +
            "SUM(CASE WHEN vg.resultScore = 'LOSE' THEN 1 ELSE 0 END) AS loseGames, " +
            "SUM(CASE WHEN vg.resultScore = 'DRAW' THEN 1 ELSE 0 END) AS drawGames " +
            "FROM VisitedGame vg WHERE vg.member = :member")
    Optional<WinningRateProjection> countWinningRate(@Param("member") Member member);
}
