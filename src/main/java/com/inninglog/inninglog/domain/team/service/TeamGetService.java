package com.inninglog.inninglog.domain.team.service;

import com.inninglog.inninglog.domain.kbo.domain.Game;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
public class TeamGetService {

    private final TeamRepository teamRepository;

    @Transactional(readOnly = true)
    public Team validateTeam(String shortCode) {
        Team team = teamRepository.findByShortCode(shortCode).orElseThrow(()->{
            log.error("️️⚠️존재하지 않는 팀 : shortCode = {}", shortCode);
            return new CustomException(ErrorCode.TEAM_NOT_FOUND);
        });
        return team;
    }

    @Transactional(readOnly = true)
    public String getOpponentTeamSC(Member member, Game game){
        String supportTeamId = member.getTeam().getShortCode();
        String opponentTeamId = "";
        //게임의 원정팀이 유저의 응원팀과 다를 경우
        if(!Objects.equals(game.getAwayTeam().getShortCode(), supportTeamId)){
            //원정팀이 상대팀
             opponentTeamId = game.getAwayTeam().getShortCode();
        }else {
            //게임의 원정팀이 유저의 응원팀과 같은 경우
            //상대팀은 홈팀이였다.
             opponentTeamId = game.getHomeTeam().getShortCode();
        }

        validateTeam(opponentTeamId);

        return opponentTeamId;
    }
}
