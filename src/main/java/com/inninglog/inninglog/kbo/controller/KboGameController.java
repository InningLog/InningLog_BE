package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.kbo.dto.KboGameDto;
import com.inninglog.inninglog.kbo.service.KboSchedulePersistenceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Tag(name = "KBO 게임 저장", description = "Python 크롤러에서 수집한 게임 데이터 저장")
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kbo")
public class KboGameController {

    private final KboSchedulePersistenceService persistenceService;

    @Operation(
            summary = "게임 일정 저장",
            description = "Python 크롤러에서 수집한 게임 일정을 데이터베이스에 저장합니다."
    )
    @PostMapping("/games")
    public ResponseEntity<Map<String, Object>> saveGames(@RequestBody Map<String, Object> request) {

        try {
            // 요청 데이터 파싱
            @SuppressWarnings("unchecked")
            List<Map<String, Object>> gamesData = (List<Map<String, Object>>) request.get("games");
            String gameDate = (String) request.get("gameDate");

            if (gamesData == null || gamesData.isEmpty()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("success", false);
                errorResponse.put("message", "게임 데이터가 없습니다");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            log.info("게임 저장 요청 - 날짜: {}, 게임 수: {}", gameDate, gamesData.size());

            // Map을 KboGameDto로 변환
            List<KboGameDto> gameDtos = gamesData.stream()
                    .map(this::mapToKboGameDto)
                    .toList();

            // 게임 저장
            persistenceService.saveGames(gameDtos, gameDate);

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "게임 저장 완료");
            response.put("data", Map.of(
                    "savedGames", gameDtos.size(),
                    "gameDate", gameDate
            ));

            log.info("게임 저장 완료 - {}개 게임", gameDtos.size());
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("게임 저장 중 오류 발생", e);

            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("message", "게임 저장 실패: " + e.getMessage());

            return ResponseEntity.internalServerError().body(errorResponse);
        }
    }

    /**
     * Map 데이터를 KboGameDto로 변환
     */
    private KboGameDto mapToKboGameDto(Map<String, Object> gameData) {
        try {
            return KboGameDto.builder()
                    .awayTeam((String) gameData.get("awayTeam"))
                    .homeTeam((String) gameData.get("homeTeam"))
                    .awayScore(getIntegerValue(gameData.get("awayScore")))
                    .homeScore(getIntegerValue(gameData.get("homeScore")))
                    .stadium((String) gameData.get("stadium"))
                    .gameDateTime((String) gameData.get("gameDateTime"))
                    .gameId((String) gameData.get("gameId"))
                    .boxscore_url((String) gameData.get("boxscoreUrl"))
                    .build();

        } catch (Exception e) {
            log.error("게임 데이터 변환 실패: {}", gameData, e);
            throw new RuntimeException("게임 데이터 변환 실패", e);
        }
    }

    /**
     * Object를 Integer로 안전하게 변환
     */
    private Integer getIntegerValue(Object value) {
        if (value == null) return 0;
        if (value instanceof Integer) return (Integer) value;
        if (value instanceof Number) return ((Number) value).intValue();
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return 0;
            }
        }
        return 0;
    }
}