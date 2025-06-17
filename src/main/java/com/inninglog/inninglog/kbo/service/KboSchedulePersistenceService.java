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

@Service
@RequiredArgsConstructor
@Slf4j
public class KboSchedulePersistenceService {

    private final TeamRepository teamRepository;
    private final StadiumRepository stadiumRepository;
    private final GameRepository gameRepository;

    public void saveGames(List<KboGameDto> gameDtos, String gameDate) {
        for (KboGameDto dto : gameDtos) {
            try {
                Team awayTeam = teamRepository.findByName(dto.getAwayTeam())
                        .orElseThrow(() -> new RuntimeException("Away 팀 찾을 수 없음: " + dto.getAwayTeam()));

                Team homeTeam = teamRepository.findByName(dto.getHomeTeam())
                        .orElseThrow(() -> new RuntimeException("Home 팀 찾을 수 없음: " + dto.getHomeTeam()));

                Stadium stadium = stadiumRepository.findByName(dto.getStadium())
                        .orElseThrow(() -> new RuntimeException("경기장 찾을 수 없음: " + dto.getStadium()));

                // 날짜 + 시간 조합
                LocalDate date = LocalDate.parse(gameDate);
                LocalTime time = LocalTime.parse(dto.getGameDateTime()); // "18:30"
                LocalDateTime localDateTime = LocalDateTime.of(date, time);

                Game game = Game.builder()
                        .awayTeam(awayTeam)
                        .homeTeam(homeTeam)
                        .stadium(stadium)
                        .localDateTime(localDateTime)
                        .awayScore(dto.getAwayScore())
                        .homeScore(dto.getHomeScore())
                        .build();

                gameRepository.save(game);

            } catch (Exception e) {
                log.warn("Game 저장 실패: {}", dto, e);
            }
        }
    }
}
