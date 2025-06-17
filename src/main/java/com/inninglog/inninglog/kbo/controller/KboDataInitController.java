package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.kbo.dto.KboGameDto;
import com.inninglog.inninglog.kbo.service.KboHtmlScheduleService;
import com.inninglog.inninglog.kbo.service.KboSchedulePersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kbo/init")
public class KboDataInitController {

    private final KboHtmlScheduleService scheduleService;
    private final KboSchedulePersistenceService persistenceService;

    @PostMapping("/save/{date}")
    public ResponseEntity<Void> saveGamesForDate(@PathVariable String date) {
        List<KboGameDto> games = scheduleService.getGamesByDate(date);
        persistenceService.saveGames(games, date);
        return ResponseEntity.ok().build();
    }
}
