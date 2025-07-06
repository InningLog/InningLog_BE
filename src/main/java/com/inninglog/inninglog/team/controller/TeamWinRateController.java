package com.inninglog.inninglog.team.controller;

import com.inninglog.inninglog.team.dto.TeamWinRateDto;
import com.inninglog.inninglog.team.service.TeamWinRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/api/kbo/team-rankings")
@RequiredArgsConstructor
@Slf4j
public class TeamWinRateController {

    private final TeamWinRateService teamWinRateService;

    /**
     * FastAPI에서 팀 승률 데이터 받아서 업데이트
     */
    @PostMapping("/win-rates")
    public ResponseEntity<TeamWinRateDto.WinRateUpdateResponse> updateTeamWinRates(
            @RequestBody TeamWinRateDto.WinRateUpdateRequest request) {

        log.info("팀 승률 업데이트 요청 수신: 날짜={}, 팀수={}", request.getDate(), request.getTotalTeams());

        try {
            TeamWinRateDto.WinRateUpdateResponse response = teamWinRateService.updateTeamWinRates(request);
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            log.error("팀 승률 업데이트 실패: {}", e.getMessage(), e);

            TeamWinRateDto.WinRateUpdateResponse errorResponse = TeamWinRateDto.WinRateUpdateResponse.builder()
                    .message("팀 승률 업데이트 실패: " + e.getMessage())
                    .updatedTeams(0)
                    .totalTeams(request.getTotalTeams())
                    .skippedTeams(0)
                    .date(request.getDate())
                    .build();

            return ResponseEntity.badRequest().body(errorResponse);
        }
    }
}