package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.kbo.dto.KboGameDto;
import com.inninglog.inninglog.kbo.service.KboSchedulePersistenceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/kbo/init")
@RequiredArgsConstructor
public class KboGameReceiveController {
    private final KboSchedulePersistenceService persistenceService;

    @PostMapping("/save/{date}")
    public ResponseEntity<Void> receiveGames(
            @PathVariable String date,
            @RequestBody List<KboGameDto> games) {
        // date 형식 검증 생략 가능
        persistenceService.saveGames(games, date);
        return ResponseEntity.ok().build();
    }
}
