package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.domain.GameStatus;
import com.inninglog.inninglog.kbo.dto.*;
import com.inninglog.inninglog.kbo.dto.gameResult.GameResultDto;
import com.inninglog.inninglog.kbo.dto.gameResult.GameResultRequestDto;
import com.inninglog.inninglog.kbo.dto.gameSchdule.GameScheduleDto;
import com.inninglog.inninglog.kbo.dto.gameSchdule.GameScheduleRequestDto;
import com.inninglog.inninglog.kbo.dto.gameSchdule.GameScheduleResponseDto;
import com.inninglog.inninglog.kbo.dto.playerstat.GameWithBoxscoreDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class GameScheduleService {

    private final GameRepository gameRepository;
    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;

    /**
     * 월별 경기 일정 저장
     */
    public GameScheduleResponseDto saveMonthlySchedule(GameScheduleRequestDto requestDto) {
        log.info("월별 일정 저장 시작: 연월={}, 경기 수={}",
                requestDto.getYearMonth(), requestDto.getGames().size());

        int savedCount = 0;
        int duplicateCount = 0;
        int errorCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (GameScheduleDto gameDto : requestDto.getGames()) {
            try {
                // 중복 체크
                Optional<Game> existingGame = gameRepository.findByGameId(gameDto.getGameId());

                if (existingGame.isPresent()) {
                    duplicateCount++;
                    log.debug("중복 게임 스킵: {}", gameDto.getGameId());
                    continue;
                }

                // 팀 조회
                Team awayTeam = findTeamByName(gameDto.getAwayTeam());
                Team homeTeam = findTeamByName(gameDto.getHomeTeam());

                // 경기장 조회
                Stadium stadium = findStadiumByName(gameDto.getStadium());

                // 날짜/시간 파싱 (수정: 더 정확한 파싱)
                LocalDateTime gameDateTime = parseGameDateTimeFromSchedule(gameDto, requestDto.getYearMonth());

                // Game 엔티티 생성
                Game game = Game.builder()
                        .gameId(gameDto.getGameId())
                        .awayTeam(awayTeam)
                        .homeTeam(homeTeam)
                        .awayScore(0)  // 일정 단계에서는 0
                        .homeScore(0)  // 일정 단계에서는 0
                        .stadium(stadium)
                        .localDateTime(gameDateTime)
                        .boxscoreUrl(null)  // 일정 단계에서는 null
                        .status(GameStatus.SCHEDULED)
                        .build();

                gameRepository.save(game);
                savedCount++;

                log.debug("일정 저장 성공: {} vs {} - 날짜시간: {}",
                        awayTeam.getName(), homeTeam.getName(), gameDateTime);

            } catch (Exception e) {
                errorCount++;
                String errorMsg = String.format("게임 %s 저장 실패: %s", gameDto.getGameId(), e.getMessage());
                errorMessages.add(errorMsg);
                log.error(errorMsg, e);
            }
        }

        String message = String.format("월별 일정 저장 완료: 저장=%d, 중복=%d, 오류=%d",
                savedCount, duplicateCount, errorCount);

        return GameScheduleResponseDto.builder()
                .success(true)
                .message(message)
                .savedCount(savedCount)
                .updatedCount(0)
                .duplicateCount(duplicateCount)
                .errorCount(errorCount)
                .errorMessages(errorMessages)
                .build();
    }

    /**
     * 경기 결과 업데이트
     */
    public GameScheduleResponseDto updateGameResults(GameResultRequestDto requestDto) {
        log.info("경기 결과 업데이트 시작: 날짜={}, 경기 수={}",
                requestDto.getGameDate(), requestDto.getGames().size());

        int updatedCount = 0;
        int savedCount = 0;
        int errorCount = 0;
        List<String> errorMessages = new ArrayList<>();

        for (GameResultDto gameDto : requestDto.getGames()) {
            try {
                Optional<Game> existingGameOpt = gameRepository.findByGameId(gameDto.getGameId());

                if (existingGameOpt.isPresent()) {
                    // 기존 경기 업데이트
                    Game existingGame = existingGameOpt.get();

                    existingGame.updateResult(
                            gameDto.getAwayScore(),
                            gameDto.getHomeScore(),
                            gameDto.getBoxscoreUrl(),
                            GameStatus.COMPLETED
                    );

                    gameRepository.save(existingGame);
                    updatedCount++;

                    log.debug("경기 결과 업데이트: {} {}:{} {} - 날짜: {}",
                            existingGame.getAwayTeam().getName(),
                            gameDto.getAwayScore(),
                            gameDto.getHomeScore(),
                            existingGame.getHomeTeam().getName(),
                            existingGame.getLocalDateTime().toLocalDate());

                } else {
                    // 새로운 경기 생성 (일정이 없었던 경우)
                    Team awayTeam = findTeamByName(gameDto.getAwayTeam());
                    Team homeTeam = findTeamByName(gameDto.getHomeTeam());
                    Stadium stadium = findStadiumByName(gameDto.getStadium());

                    // 날짜/시간 파싱 (수정: gameDate 활용)
                    LocalDateTime gameDateTime = parseGameDateTimeFromResult(gameDto, requestDto.getGameDate());

                    Game newGame = Game.builder()
                            .gameId(gameDto.getGameId())
                            .awayTeam(awayTeam)
                            .homeTeam(homeTeam)
                            .awayScore(gameDto.getAwayScore())
                            .homeScore(gameDto.getHomeScore())
                            .stadium(stadium)
                            .localDateTime(gameDateTime)
                            .boxscoreUrl(gameDto.getBoxscoreUrl())
                            .status(GameStatus.COMPLETED)
                            .build();

                    gameRepository.save(newGame);
                    savedCount++;

                    log.debug("새 경기 결과 저장: {} {}:{} {} - 날짜시간: {}",
                            awayTeam.getName(),
                            gameDto.getAwayScore(),
                            gameDto.getHomeScore(),
                            homeTeam.getName(),
                            gameDateTime);
                }

            } catch (Exception e) {
                errorCount++;
                String errorMsg = String.format("게임 %s 결과 업데이트 실패: %s", gameDto.getGameId(), e.getMessage());
                errorMessages.add(errorMsg);
                log.error(errorMsg, e);
            }
        }

        String message = String.format("경기 결과 업데이트 완료: 업데이트=%d, 신규=%d, 오류=%d",
                updatedCount, savedCount, errorCount);

        return GameScheduleResponseDto.builder()
                .success(true)
                .message(message)
                .savedCount(savedCount)
                .updatedCount(updatedCount)
                .duplicateCount(0)
                .errorCount(errorCount)
                .errorMessages(errorMessages)
                .build();
    }

    /**
     * 박스스코어 URL이 있는 경기 조회
     */
    @Transactional(readOnly = true)
    public List<GameWithBoxscoreDto> getGamesWithBoxscore(String gameDate) {
        LocalDate date = LocalDate.parse(gameDate);

        List<Game> games = gameRepository.findByLocalDateTimeAndBoxscoreUrlIsNotNull(date);

        return games.stream()
                .map(game -> GameWithBoxscoreDto.builder()
                        .gameId(game.getGameId())
                        .awayTeam(game.getAwayTeam().getName())
                        .homeTeam(game.getHomeTeam().getName())
                        .boxscoreUrl(game.getBoxscoreUrl())
                        .gameDateTime(game.getLocalDateTime().format(DateTimeFormatter.ofPattern("HH:mm")))
                        .stadium(game.getStadium().getName())
                        .build())
                .toList();
    }

    /**
     * 월별 경기 통계 조회
     */
    @Transactional(readOnly = true)
    public MonthlyGameStatsDto getMonthlyGameStats(String yearMonth) {
        String[] parts = yearMonth.split("-");
        int year = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);

        LocalDate startDate = LocalDate.of(year, month, 1);
        LocalDate endDate = startDate.plusMonths(1).minusDays(1);

        List<Game> games = gameRepository.findByLocalDateTimeBetween(startDate, endDate);

        int totalGames = games.size();
        int scheduledGames = (int) games.stream().filter(g -> g.getStatus() == GameStatus.SCHEDULED).count();
        int completedGames = (int) games.stream().filter(g -> g.getStatus() == GameStatus.COMPLETED).count();
        int gamesWithBoxscore = (int) games.stream().filter(g -> g.getBoxscoreUrl() != null).count();

        // 선수 기록 있는 경기 수는 별도 쿼리 필요 (PlayerStat 테이블 참조)
        int gamesWithPlayerStats = gameRepository.countGamesWithPlayerStats(startDate, endDate);

        return MonthlyGameStatsDto.builder()
                .yearMonth(yearMonth)
                .totalGames(totalGames)
                .scheduledGames(scheduledGames)
                .completedGames(completedGames)
                .gamesWithBoxscore(gamesWithBoxscore)
                .gamesWithPlayerStats(gamesWithPlayerStats)
                .lastUpdated(LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")))
                .build();
    }

    // ================================
    // Private 헬퍼 메서드들
    // ================================

    private Team findTeamByName(String teamName) {
        return teamRepository.findByName(teamName)
                .orElseThrow(() -> new IllegalArgumentException("팀을 찾을 수 없습니다: " + teamName));
    }

    private Stadium findStadiumByName(String stadiumName) {
        Map<String, String> stadiumMap = Map.of(
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

        String mappedName = stadiumMap.getOrDefault(stadiumName, stadiumName);

        return stadiumRepository.findByName(mappedName)
                .orElseThrow(() -> new IllegalArgumentException("경기장을 찾을 수 없습니다: " + stadiumName));
    }

    /**
     * 월별 일정용 날짜시간 파싱
     */
    private LocalDateTime parseGameDateTimeFromSchedule(GameScheduleDto gameDto, String yearMonth) {
        // 1차: gameId에서 날짜 추출 시도
        LocalDate gameDate = extractDateFromGameId(gameDto.getGameId());

        // 2차: yearMonth와 일치하는지 확인
        if (gameDate != null) {
            String gameYearMonth = gameDate.format(DateTimeFormatter.ofPattern("yyyy-MM"));
            if (!gameYearMonth.equals(yearMonth)) {
                log.warn("gameId 날짜({})와 요청 연월({})이 다름", gameYearMonth, yearMonth);
            }
        } else {
            // gameId에서 추출 실패시 기본값
            gameDate = LocalDate.now();
            log.warn("gameId에서 날짜 추출 실패, 현재 날짜 사용: {}", gameDate);
        }

        return parseTime(gameDto.getGameDateTime(), gameDate);
    }

    /**
     * 경기 결과용 날짜시간 파싱
     */
    private LocalDateTime parseGameDateTimeFromResult(GameResultDto gameDto, String gameDate) {
        // 1차: gameDate 파라미터 사용
        LocalDate targetDate;
        try {
            targetDate = LocalDate.parse(gameDate); // "2025-06-01"
        } catch (Exception e) {
            log.warn("gameDate 파싱 실패: {}, gameId에서 추출 시도", gameDate);
            // 2차: gameId에서 날짜 추출
            targetDate = extractDateFromGameId(gameDto.getGameId());
            if (targetDate == null) {
                targetDate = LocalDate.now();
                log.warn("모든 날짜 추출 실패, 현재 날짜 사용: {}", targetDate);
            }
        }

        return parseTime(gameDto.getGameDateTime(), targetDate);
    }

    /**
     * gameId에서 날짜 추출: "20250601LTWS01" → LocalDate.of(2025, 6, 1)
     */
    private LocalDate extractDateFromGameId(String gameId) {
        if (gameId == null || gameId.length() < 8) {
            return null;
        }

        try {
            String dateStr = gameId.substring(0, 8); // "20250601"
            int year = Integer.parseInt(dateStr.substring(0, 4));   // 2025
            int month = Integer.parseInt(dateStr.substring(4, 6));  // 06
            int day = Integer.parseInt(dateStr.substring(6, 8));    // 01

            LocalDate result = LocalDate.of(year, month, day);
            log.debug("gameId에서 날짜 추출: {} → {}", dateStr, result);
            return result;

        } catch (Exception e) {
            log.warn("gameId 날짜 파싱 실패: {}", gameId);
            return null;
        }
    }

    /**
     * 시간 파싱: "14:00" + LocalDate → LocalDateTime
     */
    private LocalDateTime parseTime(String timeStr, LocalDate date) {
        try {
            String[] timeParts = timeStr.split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            LocalDateTime result = date.atTime(hour, minute);
            log.debug("시간 파싱 완료: {} + {} → {}", date, timeStr, result);
            return result;

        } catch (Exception e) {
            log.warn("시간 파싱 실패: {}, 기본값(14:00) 사용", timeStr);
            return date.atTime(14, 0); // 기본값: 오후 2시
        }
    }
}