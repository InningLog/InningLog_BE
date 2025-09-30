package com.inninglog.inninglog.domain.home.service;

import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.dto.gameReport.GameHomeResDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class HomeService {

    // 유저의 응원팀 전체 경기 일정 조회
    public List<GameHomeResDto> getAllGamesForTeam(List<Game> games, Long teamId) {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");

        return games.stream()
                .map(g -> {
                    boolean isHomeTeam = g.getHomeTeam().getId().equals(teamId);
                    String myTeam = isHomeTeam ? g.getHomeTeam().getShortCode() : g.getAwayTeam().getShortCode();
                    String opponentTeam = isHomeTeam ? g.getAwayTeam().getShortCode() : g.getHomeTeam().getShortCode();
                    String formattedDateTime = g.getLocalDateTime().format(formatter);

                    return GameHomeResDto.from(
                            myTeam,
                            opponentTeam,
                            g.getStadium().getShortCode(),
                            formattedDateTime
                    );
                })
                .toList();
    }
}