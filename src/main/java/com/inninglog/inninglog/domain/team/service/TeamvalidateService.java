package com.inninglog.inninglog.domain.team.service;

import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamvalidateService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Team validateTeam(String shortCode) {
        Team team = teamRepository.findByShortCode(shortCode).orElseThrow(()->{
            log.error("️️⚠️존재하지 않는 팀 : shortCode = {}", shortCode);
            return new CustomException(ErrorCode.TEAM_NOT_FOUND);
        });
        return team;
    }
}
