package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.kbo.dto.KboGameDto;
import com.inninglog.inninglog.kbo.service.KboHtmlScheduleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/kbo/html")
public class KboScheduleHtmlController {

    private final KboHtmlScheduleService scheduleService;

    /**
     * 특정 날짜의 KBO 경기 일정을 조회합니다.
     *
     * @param date 조회할 날짜 (YYYY-MM-DD 형식, 선택사항)
     * @return KBO 경기 목록
     */
    @GetMapping
    public ResponseEntity<List<KboGameDto>> getGamesByDate(
            @RequestParam(required = false)
            @DateTimeFormat(pattern = "yyyy-MM-dd")
            String date) {

        try {
            log.info("KBO 스케줄 조회 요청 - 날짜: {}", date != null ? date : "오늘");

            // 날짜 유효성 검증 (선택사항)
            if (date != null && !date.isBlank()) {
                try {
                    LocalDate.parse(date);
                } catch (DateTimeParseException e) {
                    log.warn("잘못된 날짜 형식: {}", date);
                    return ResponseEntity.badRequest().build();
                }
            }

            List<KboGameDto> games = scheduleService.getGamesByDate(date);
            log.info("조회된 경기 수: {}", games.size());

            return ResponseEntity.ok(games);

        } catch (Exception e) {
            log.error("KBO 스케줄 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 오늘의 KBO 경기 일정을 조회합니다.
     */
    @GetMapping("/today")
    public ResponseEntity<List<KboGameDto>> getTodayGames() {
        return getGamesByDate(null);
    }

    /**
     * 특정 날짜 범위의 KBO 경기 일정을 조회합니다.
     *
     * @param startDate 시작 날짜 (YYYY-MM-DD)
     * @param endDate 종료 날짜 (YYYY-MM-DD)
     * @return KBO 경기 목록
     */
    @GetMapping("/range")
    public ResponseEntity<List<KboGameDto>> getGamesByDateRange(
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String startDate,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM-dd") String endDate) {

        try {
            log.info("KBO 스케줄 범위 조회 요청 - {} ~ {}", startDate, endDate);

            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            // 날짜 범위 유효성 검증
            if (start.isAfter(end)) {
                log.warn("잘못된 날짜 범위: {} > {}", startDate, endDate);
                return ResponseEntity.badRequest().build();
            }

            // 너무 긴 범위 방지 (예: 최대 30일)
            if (start.plusDays(30).isBefore(end)) {
                log.warn("날짜 범위가 너무 깁니다: {} ~ {}", startDate, endDate);
                return ResponseEntity.badRequest().build();
            }

            List<KboGameDto> allGames = scheduleService.getGamesByDateRange(startDate, endDate);
            log.info("조회된 총 경기 수: {}", allGames.size());

            return ResponseEntity.ok(allGames);

        } catch (DateTimeParseException e) {
            log.warn("잘못된 날짜 형식: startDate={}, endDate={}", startDate, endDate);
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("KBO 스케줄 범위 조회 중 오류 발생", e);
            return ResponseEntity.internalServerError().build();
        }
    }

    /**
     * 특정 팀의 경기 일정을 조회합니다.
     *
     * @param teamName 팀명 (예: "두산", "삼성")
     * @param date 조회할 날짜 (선택사항)
     * @return 해당 팀의 경기 목록
     */
    @GetMapping("/team/{teamName}")
    public ResponseEntity<List<KboGameDto>> getGamesByTeam(
            @PathVariable String teamName,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd") String date) {

        try {
            log.info("팀별 KBO 스케줄 조회 요청 - 팀: {}, 날짜: {}", teamName, date != null ? date : "오늘");

            List<KboGameDto> allGames = scheduleService.getGamesByDate(date);
            List<KboGameDto> teamGames = allGames.stream()
                    .filter(game -> teamName.equals(game.getAwayTeam()) || teamName.equals(game.getHomeTeam()))
                    .toList();

            log.info("조회된 {} 팀 경기 수: {}", teamName, teamGames.size());
            return ResponseEntity.ok(teamGames);

        } catch (Exception e) {
            log.error("팀별 KBO 스케줄 조회 중 오류 발생 - 팀: {}", teamName, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}