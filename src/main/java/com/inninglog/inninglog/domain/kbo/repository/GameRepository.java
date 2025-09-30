package com.inninglog.inninglog.domain.kbo.repository;

import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.domain.GameStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {

    /**
     * gameId로 경기 조회
     */
    Optional<Game> findByGameId(String gameId);

    /**
     * 특정 날짜의 경기 조회 (DATE 함수 사용)
     */
    @Query("SELECT g FROM Game g WHERE DATE(g.localDateTime) = :date ORDER BY g.localDateTime")
    List<Game> findByLocalDateTime(@Param("date") LocalDate date);

    /**
     * 특정 날짜의 특정 팀 경기 조회
     */
    @Query("""
    SELECT g FROM Game g 
    WHERE DATE(g.localDateTime) = :date 
      AND (g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId) 
    ORDER BY g.localDateTime
""")
   Game findByDateAndTeamId(
            @Param("date") LocalDate date,
            @Param("teamId") Long teamId
    );

    /**
     * 특정 날짜의 박스스코어 URL이 있는 경기 조회
     */
    @Query("SELECT g FROM Game g WHERE DATE(g.localDateTime) = :date AND g.boxscoreUrl IS NOT NULL ORDER BY g.localDateTime")
    List<Game> findByLocalDateTimeAndBoxscoreUrlIsNotNull(@Param("date") LocalDate date);

    /**
     * 날짜 범위 내 경기 조회 (DATE 함수 사용)
     */
    @Query("SELECT g FROM Game g WHERE DATE(g.localDateTime) BETWEEN :startDate AND :endDate ORDER BY g.localDateTime")
    List<Game> findByLocalDateTimeBetween(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 특정 날짜와 상태의 경기 조회
     */
    @Query("SELECT g FROM Game g WHERE DATE(g.localDateTime) = :date AND g.status = :status ORDER BY g.localDateTime")
    List<Game> findByLocalDateTimeAndStatus(@Param("date") LocalDate date, @Param("status") GameStatus status);

    /**
     * 박스스코어 URL이 있는 경기 수 조회
     */
    @Query("SELECT COUNT(g) FROM Game g WHERE DATE(g.localDateTime) BETWEEN :startDate AND :endDate AND g.boxscoreUrl IS NOT NULL")
    int countByLocalDateTimeBetweenAndBoxscoreUrlIsNotNull(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 선수 기록이 있는 경기 수 조회
     */
    @Query("SELECT COUNT(DISTINCT g) FROM Game g " +
            "JOIN PlayerStat ps ON ps.game = g " +
            "WHERE DATE(g.localDateTime) BETWEEN :startDate AND :endDate")
    int countGamesWithPlayerStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 특정 월의 경기 통계 조회
     */
    @Query("SELECT " +
            "COUNT(g) as totalGames, " +
            "SUM(CASE WHEN g.status = 'SCHEDULED' THEN 1 ELSE 0 END) as scheduledGames, " +
            "SUM(CASE WHEN g.status = 'COMPLETED' THEN 1 ELSE 0 END) as completedGames, " +
            "SUM(CASE WHEN g.boxscoreUrl IS NOT NULL THEN 1 ELSE 0 END) as gamesWithBoxscore " +
            "FROM Game g " +
            "WHERE DATE(g.localDateTime) BETWEEN :startDate AND :endDate")
    Object[] getMonthlyGameStats(@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 팀별 경기 조회
     */
    @Query("SELECT g FROM Game g WHERE (g.awayTeam.id = :teamId OR g.homeTeam.id = :teamId) AND DATE(g.localDateTime) BETWEEN :startDate AND :endDate ORDER BY g.localDateTime")
    List<Game> findByTeamAndDateRange(@Param("teamId") Long teamId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 경기장별 경기 조회
     */
    @Query("SELECT g FROM Game g WHERE g.stadium.id = :stadiumId AND DATE(g.localDateTime) BETWEEN :startDate AND :endDate ORDER BY g.localDateTime")
    List<Game> findByStadiumAndDateRange(@Param("stadiumId") Long stadiumId, @Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

    /**
     * 최근 업데이트된 경기 조회
     */
    @Query("SELECT g FROM Game g WHERE g.updatedAt >= :since ORDER BY g.updatedAt DESC")
    List<Game> findRecentlyUpdated(@Param("since") LocalDateTime since);

    /**
     * gameId 중복 체크
     */
    boolean existsByGameId(String gameId);

    /**
     * 특정 날짜에 박스스코어 URL이 없는 완료된 경기 조회
     */
    @Query("SELECT g FROM Game g WHERE DATE(g.localDateTime) = :date AND g.status = 'COMPLETED' AND g.boxscoreUrl IS NULL")
    List<Game> findCompletedGamesWithoutBoxscore(@Param("date") LocalDate date);


 @Query("""
    SELECT g FROM Game g
    WHERE g.homeTeam.id = :teamId OR g.awayTeam.id = :teamId
    ORDER BY g.localDateTime ASC
""")
 List<Game> findByTeam(@Param("teamId") Long teamId);
}