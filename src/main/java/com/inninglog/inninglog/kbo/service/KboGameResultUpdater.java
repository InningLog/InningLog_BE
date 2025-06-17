package com.inninglog.inninglog.kbo.service;

import com.inninglog.inninglog.kbo.domain.Game;
import com.inninglog.inninglog.kbo.dto.KboGameDto;
import com.inninglog.inninglog.kbo.repository.GameRepository;
import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class KboGameResultUpdater {

    private final KboHtmlScheduleService scheduleCrawler;
    private final GameRepository gameRepository;
    private final StadiumRepository stadiumRepository;

    /**
     * 매일 밤 12시 10분에 어제 경기의 결과를 업데이트 (스코어 + 경기장 + 리뷰 링크)
     */
    @Scheduled(cron = "0 10 0 * * *", zone = "Asia/Seoul")
    @Transactional
    public void updateYesterdayGameResults() {
        String yesterday = LocalDate.now().minusDays(1).toString();
        updateGameResultsByDate(yesterday);
    }

    /**
     * [테스트용] 지정한 날짜의 경기 결과를 업데이트
     *
     * @param date 날짜 (예: "2025-06-15")
     */
    @Transactional
    public void updateGameResultsByDate(String date) {
        log.info("⚾ [KBO] 지정일자 경기 결과 업데이트 시작: {}", date);

        List<KboGameDto> crawledGames = scheduleCrawler.getGamesByDate(date);

        // 날짜 범위 계산
        LocalDate localDate = LocalDate.parse(date);
        LocalDateTime startOfDay = localDate.atStartOfDay();
        LocalDateTime endOfDay = localDate.atTime(23, 59, 59);

        List<Game> existingGames = gameRepository.findAllByLocalDateTimeBetween(startOfDay, endOfDay);

        for (Game game : existingGames) {
            Optional<KboGameDto> maybeMatch = crawledGames.stream()
                    .filter(dto ->
                            dto.getHomeTeam().equals(game.getHomeTeam().getName()) &&
                                    dto.getAwayTeam().equals(game.getAwayTeam().getName()))
                    .findFirst();

            if (maybeMatch.isPresent()) {
                KboGameDto dto = maybeMatch.get();

                try {
                    game.setHomeScore(dto.getHomeScore());
                    game.setAwayScore(dto.getAwayScore());

                    Stadium stadium = stadiumRepository.findByName(dto.getStadium())
                            .orElseThrow(() -> new RuntimeException("경기장 이름 일치 안됨: " + dto.getStadium()));
                    game.setStadium(stadium);

                    game.setBoxscore_url(dto.getBoxscore_url());
                    gameRepository.save(game);
                    log.info("경기 업데이트 성공: {} vs {}", dto.getAwayTeam(), dto.getHomeTeam());
                } catch (Exception e) {
                    log.warn("경기 업데이트 실패: {} vs {} → {}", game.getAwayTeam().getName(), game.getHomeTeam().getName(), e.getMessage());
                }

            } else {
                log.warn(" 매칭 실패: {} vs {}", game.getAwayTeam().getName(), game.getHomeTeam().getName());
            }
        }

        log.info("🏁 [KBO] 경기 결과 업데이트 완료");
    }
}