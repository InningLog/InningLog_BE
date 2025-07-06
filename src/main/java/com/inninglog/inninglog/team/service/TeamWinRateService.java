package com.inninglog.inninglog.team.service;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.dto.TeamWinRateDto;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class TeamWinRateService {

    private final TeamRepository teamRepository;

    /**
     * FastAPI에서 받은 승률 데이터로 팀 승률 업데이트
     */
    @Transactional
    public TeamWinRateDto.WinRateUpdateResponse updateTeamWinRates(TeamWinRateDto.WinRateUpdateRequest request) {
        log.info("팀 승률 업데이트 시작: {} ({}개 팀)", request.getDate(), request.getTotalTeams());

        List<TeamWinRateDto.TeamUpdateDetail> teamDetails = new ArrayList<>();
        int updatedCount = 0;
        int skippedCount = 0;

        try {
            for (TeamWinRateDto winRateDto : request.getWinRates()) {
                TeamWinRateDto.TeamUpdateDetail detail = updateSingleTeamWinRate(winRateDto);
                teamDetails.add(detail);

                if ("UPDATED".equals(detail.getStatus())) {
                    updatedCount++;
                } else if ("SKIPPED".equals(detail.getStatus())) {
                    skippedCount++;
                }
            }

            log.info("팀 승률 업데이트 완료: 업데이트={}, 건너뜀={}, 총={}개",
                    updatedCount, skippedCount, request.getTotalTeams());

            return TeamWinRateDto.WinRateUpdateResponse.builder()
                    .message("팀 승률 업데이트 완료")
                    .updatedTeams(updatedCount)
                    .totalTeams(request.getTotalTeams())
                    .skippedTeams(skippedCount)
                    .date(request.getDate())
                    .processedAt(LocalDateTime.now())
                    .teamDetails(teamDetails)
                    .build();

        } catch (Exception e) {
            log.error("팀 승률 업데이트 실패: {}", e.getMessage(), e);
            throw new RuntimeException("팀 승률 업데이트 중 오류 발생", e);
        }
    }

    /**
     * 개별 팀 승률 업데이트
     */
    private TeamWinRateDto.TeamUpdateDetail updateSingleTeamWinRate(TeamWinRateDto winRateDto) {
        String teamName = winRateDto.getTeam();
        Double newWinRate = winRateDto.getWinRate();

        log.debug("팀 승률 업데이트 시도: {} -> {}", teamName, newWinRate);

        // 팀 찾기 (팀명 또는 shortCode로)
        Optional<Team> teamOptional = teamRepository.findByNameOrShortCodeIgnoreCase(teamName);

        if (teamOptional.isEmpty()) {
            log.warn("팀을 찾을 수 없음: {}", teamName);
            return TeamWinRateDto.TeamUpdateDetail.builder()
                    .teamName(teamName)
                    .shortCode(null)
                    .oldWinRate(null)
                    .newWinRate(newWinRate)
                    .status("NOT_FOUND")
                    .message("팀을 찾을 수 없습니다")
                    .build();
        }

        Team team = teamOptional.get();
        Double oldWinRate = team.getWinRate();

        // 승률이 동일하면 건너뛰기
        if (oldWinRate != null && oldWinRate.equals(newWinRate)) {
            log.debug("승률 변경 없음: {} ({})", teamName, newWinRate);
            return TeamWinRateDto.TeamUpdateDetail.builder()
                    .teamName(team.getName())
                    .shortCode(team.getShortCode())
                    .oldWinRate(oldWinRate)
                    .newWinRate(newWinRate)
                    .status("SKIPPED")
                    .message("승률 변경 없음")
                    .build();
        }

        // 승률 업데이트
        team.updateWinRate(newWinRate);
        teamRepository.save(team);

        log.info("팀 승률 업데이트 완료: {} ({} -> {})", team.getName(), oldWinRate, newWinRate);

        return TeamWinRateDto.TeamUpdateDetail.builder()
                .teamName(team.getName())
                .shortCode(team.getShortCode())
                .oldWinRate(oldWinRate)
                .newWinRate(newWinRate)
                .status("UPDATED")
                .message("승률 업데이트 성공")
                .build();
    }
}