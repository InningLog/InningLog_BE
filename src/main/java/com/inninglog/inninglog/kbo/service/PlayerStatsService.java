package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.domain.Player;
import com.inninglog.inninglog.kbo.domain.PlayerStat;
import com.inninglog.inninglog.kbo.domain.PlayerType;
import com.inninglog.inninglog.kbo.dto.HitterStatDto;
import com.inninglog.inninglog.kbo.dto.PitcherStatDto;
import com.inninglog.inninglog.kbo.dto.PlayerStatsSaveResult;
import com.inninglog.inninglog.kbo.dto.ReviewStatsDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.kbo.repository.PlayerRepository;
import com.inninglog.inninglog.kbo.repository.PlayerStatRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PlayerStatsService {

    private final PlayerRepository playerRepository;
    private final PlayerStatRepository playerStatRepository;
    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;

    public PlayerStatsSaveResult savePlayerStats(String gameId, ReviewStatsDto reviewStatsDto) {
        log.info("선수 기록 저장 시작 - gameId: {}, 투수: {}명, 타자: {}명",
                gameId, reviewStatsDto.getPitchers().size(), reviewStatsDto.getHitters().size());

        // 1. 게임 조회
        Game game = gameRepository.findByGameId(gameId)
                .orElseThrow(() -> new IllegalArgumentException("게임을 찾을 수 없습니다: " + gameId));

        // 새로 생성된 선수 추적용
        Set<Long> newPlayerIds = new HashSet<>();
        int pitcherStatsCount = 0;
        int hitterStatsCount = 0;

        // 2. 투수 기록 처리
        for (PitcherStatDto pitcherDto : reviewStatsDto.getPitchers()) {
            try {
                Player player = findOrCreatePlayer(pitcherDto.getPlayerName(),
                        pitcherDto.getTeam(),
                        PlayerType.PITCHER,
                        newPlayerIds);

                // 중복 기록 체크
                boolean exists = playerStatRepository.existsByGameAndPlayerAndPlayerType(
                        game, player, PlayerType.PITCHER);

                if (!exists) {
                    PlayerStat pitcherStat = PlayerStat.builder()
                            .game(game)
                            .player(player)
                            .playerType(PlayerType.PITCHER)
                            .inning(parseInnings(pitcherDto.getInnings()))
                            .earned(pitcherDto.getEarnedRuns())
                            .hits(0)      // 투수는 안타/타수 0
                            .at_bats(0)
                            .build();

                    playerStatRepository.save(pitcherStat);
                    pitcherStatsCount++;
                    log.debug("투수 기록 저장: {} - {}이닝, {}자책",
                            player.getName(), pitcherDto.getInnings(), pitcherDto.getEarnedRuns());
                } else {
                    log.debug("투수 기록 중복 스킵: {}", player.getName());
                }

            } catch (Exception e) {
                log.error("투수 기록 처리 중 오류: {}", pitcherDto.getPlayerName(), e);
            }
        }

        // 3. 타자 기록 처리
        for (HitterStatDto hitterDto : reviewStatsDto.getHitters()) {
            try {
                Player player = findOrCreatePlayer(hitterDto.getPlayerName(),
                        hitterDto.getTeam(),
                        PlayerType.HITTER,
                        newPlayerIds);

                // 중복 기록 체크
                boolean exists = playerStatRepository.existsByGameAndPlayerAndPlayerType(
                        game, player, PlayerType.HITTER);

                if (!exists) {
                    PlayerStat hitterStat = PlayerStat.builder()
                            .game(game)
                            .player(player)
                            .playerType(PlayerType.HITTER)
                            .inning(0.0)  // 타자는 이닝/자책 0
                            .earned(0)
                            .hits(hitterDto.getHits())
                            .at_bats(hitterDto.getAtBats())
                            .build();

                    playerStatRepository.save(hitterStat);
                    hitterStatsCount++;
                    log.debug("타자 기록 저장: {} - {}타수, {}안타",
                            player.getName(), hitterDto.getAtBats(), hitterDto.getHits());
                } else {
                    log.debug("타자 기록 중복 스킵: {}", player.getName());
                }

            } catch (Exception e) {
                log.error("타자 기록 처리 중 오류: {}", hitterDto.getPlayerName(), e);
            }
        }

        int newPlayersCount = newPlayerIds.size();
        log.info("선수 기록 저장 완료 - 새 선수: {}명, 투수 기록: {}건, 타자 기록: {}건",
                newPlayersCount, pitcherStatsCount, hitterStatsCount);

        return new PlayerStatsSaveResult(newPlayersCount, pitcherStatsCount, hitterStatsCount);
    }

    /**
     * 선수를 찾거나 새로 생성합니다.
     */
    private Player findOrCreatePlayer(String playerName, String teamName, PlayerType playerType, Set<Long> newPlayerIds) {
        // 팀 조회
        Team team = findTeamByKboName(teamName);

        // 기존 선수 조회 (이름 + 팀으로)
        Optional<Player> existingPlayer = playerRepository.findByNameAndTeam(playerName, team);

        if (existingPlayer.isPresent()) {
            return existingPlayer.get();
        } else {
            // 새 선수 생성
            Player newPlayer = Player.builder()
                    .name(playerName)
                    .team(team)
                    .playerType(playerType)
                    .build();

            Player savedPlayer = playerRepository.save(newPlayer);
            newPlayerIds.add(savedPlayer.getId()); // 새 선수 ID 추적
            log.info("새 선수 등록: {} ({}, {})", playerName, teamName, playerType);
            return savedPlayer;
        }
    }

    /**
     * KBO 크롤링 팀명을 DB 팀명으로 매핑하여 조회
     */
    private Team findTeamByKboName(String kboTeamName) {
        // 크롤링 팀명 → DB 팀명 매핑 (KboSchedulePersistenceService와 동일)
        Map<String, String> teamNameMap = Map.of(
                "두산", "두산",
                "LG", "LG",
                "키움", "키움",
                "KT", "KT",
                "SSG", "SSG",
                "롯데", "롯데",
                "삼성", "삼성",
                "한화", "한화",
                "KIA", "KIA",
                "NC", "NC"
        );

        String dbTeamName = teamNameMap.getOrDefault(kboTeamName, kboTeamName);

        return teamRepository.findByName(dbTeamName)
                .orElseThrow(() -> new IllegalArgumentException(
                        String.format("팀을 찾을 수 없습니다: %s (매핑명: %s)", kboTeamName, dbTeamName)));
    }

    /**
     * 이닝 문자열을 double로 변환합니다.
     * 예: "5.2" → 5.67 (5와 2/3이닝)
     */
    private double parseInnings(String innings) {
        try {
            if (innings.contains(".")) {
                String[] parts = innings.split("\\.");
                double wholeInnings = Double.parseDouble(parts[0]);
                double fractionalOuts = Double.parseDouble(parts[1]);
                return wholeInnings + (fractionalOuts / 3.0); // 1/3이닝 단위
            } else {
                return Double.parseDouble(innings);
            }
        } catch (Exception e) {
            log.warn("이닝 파싱 실패: {}", innings);
            return 0.0;
        }
    }
}