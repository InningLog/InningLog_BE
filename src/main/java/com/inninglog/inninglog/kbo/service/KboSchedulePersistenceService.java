package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.KboGameDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class KboSchedulePersistenceService {

    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final GameRepository gameRepository;

    public void saveGames(List<KboGameDto> gameDtos, String gameDate) {
        log.info("게임 저장 시작: {}개, 날짜: {}", gameDtos.size(), gameDate);

        for (KboGameDto dto : gameDtos) {
            try {
                // 1. gameId 필수 체크
                if (dto.getGameId() == null || dto.getGameId().trim().isEmpty()) {
                    log.warn("gameId가 null입니다. 스킵: {}", dto);
                    continue;
                }

                // 2. 중복 게임 체크
                if (gameRepository.existsByGameId(dto.getGameId())) {
                    log.debug("이미 존재하는 게임. 스킵: {}", dto.getGameId());
                    continue;
                }

                // 3. 팀 조회 (크롤링 팀명 → DB 팀명 매핑)
                Team awayTeam = findTeamByKboName(dto.getAwayTeam());
                Team homeTeam = findTeamByKboName(dto.getHomeTeam());

                // 4. 경기장 조회 (크롤링 경기장명 → DB 경기장명 매핑)
                Stadium stadium = findStadiumByKboName(dto.getStadium());

                // 5. 날짜 + 시간 조합
                LocalDate date = LocalDate.parse(gameDate);
                LocalTime time = LocalTime.parse(dto.getGameDateTime()); // "18:30"
                LocalDateTime localDateTime = LocalDateTime.of(date, time);

                // 6. Game 엔티티 생성 및 저장
                Game game = Game.builder()
                        .gameId(dto.getGameId())              // 필수!
                        .awayTeam(awayTeam)
                        .homeTeam(homeTeam)
                        .stadium(stadium)
                        .localDateTime(localDateTime)
                        .awayScore(dto.getAwayScore())
                        .homeScore(dto.getHomeScore())
                        .boxscore_url(dto.getBoxscore_url())    // 선수 기록 크롤링용
                        .build();

                gameRepository.save(game);
                log.info("게임 저장 성공: {} vs {} ({})",
                        dto.getAwayTeam(), dto.getHomeTeam(), dto.getGameId());

            } catch (Exception e) {
                log.error("Game 저장 실패: {}", dto, e);
            }
        }
        log.info("게임 저장 완료");
    }

    /**
     * KBO에서 크롤링한 팀명을 DB 팀명으로 매핑하여 조회
     */
    private Team findTeamByKboName(String kboTeamName) {
        // 크롤링 팀명 → DB 팀명 매핑
        Map<String, String> teamNameMap = Map.of(
                "두산", "두산",     // 크롤링: "두산" → DB: "두산"
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
                .orElseThrow(() -> new RuntimeException(
                        String.format("팀을 찾을 수 없음: %s (매핑명: %s)", kboTeamName, dbTeamName)));
    }

    /**
     * KBO에서 크롤링한 경기장명을 DB 경기장명으로 매핑하여 조회
     */
    private Stadium findStadiumByKboName(String kboStadiumName) {
        // 크롤링 경기장명 → DB 경기장명 매핑
        Map<String, String> stadiumNameMap = Map.of(
                "잠실", "잠실",         // 크롤링: "잠실" → DB: "잠실"
                "고척", "고척",
                "문학", "문학",
                "수원", "수원",
                "대전(신)", "대전(신)",
                "대구", "대구",
                "사직", "사직",
                "창원", "창원",
                "광주", "광주"
        );

        String dbStadiumName = stadiumNameMap.getOrDefault(kboStadiumName, kboStadiumName);

        return stadiumRepository.findByName(dbStadiumName)
                .orElseThrow(() -> new RuntimeException(
                        String.format("경기장을 찾을 수 없음: %s (매핑명: %s)", kboStadiumName, dbStadiumName)));
    }
}