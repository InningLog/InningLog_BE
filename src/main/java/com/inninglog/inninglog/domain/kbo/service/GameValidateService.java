package com.inninglog.inninglog.domain.kbo.service;

import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.kbo.repository.GameRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class GameValidateService {

    private final GameRepository gameRepository;

    @Transactional(readOnly = true)
    public List<Game> findByTeam(Long teamId){
        List<Game> games = gameRepository.findByTeam(teamId);

        if (games.isEmpty()) {
            log.warn("📌 [getAllGamesForTeam] ⚠️ 팀 경기 일정이 없습니다. teamId={}", teamId);
        } else {
            log.info("📌 [getAllGamesForTeam] 📅 전체 경기 {}건 조회됨. teamId={}", games.size(), teamId);
        }

        return games;
    }

    @Transactional(readOnly = true)
    public Game findById(String gameId){
        return gameRepository.findByGameId(gameId)
                .orElseThrow(() -> {
                    log.warn("⚠️ 존재하지 않는 경기: gameId={}", gameId);
                    return new CustomException(ErrorCode.GAME_NOT_FOUND);
                });
    }
}
