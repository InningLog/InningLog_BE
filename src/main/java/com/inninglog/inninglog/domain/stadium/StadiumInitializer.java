package com.inninglog.inninglog.domain.stadium;

import com.inninglog.inninglog.domain.stadium.domain.Stadium;
import com.inninglog.inninglog.domain.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.domain.team.domain.Team;
import com.inninglog.inninglog.domain.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
@Order(2)
public class StadiumInitializer implements ApplicationRunner {

    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (stadiumRepository.count() == 0) {
            Map<String, Team> teams = Stream.of("OB", "WO", "SK", "KT", "HH", "SS", "LT", "NC", "HT", "LG")
                    .collect(Collectors.toMap(code -> code, code ->
                            teamRepository.findByShortCode(code)
                                    .orElseThrow(() -> new RuntimeException(code + " 팀이 존재하지 않습니다."))));

            stadiumRepository.saveAll(List.of(
                    Stadium.builder().name("잠실").shortCode("JAM").team(teams.get("LG")).build(),       // LG
                    Stadium.builder().name("고척").shortCode("GOC").team(teams.get("WO")).build(),       // 키움
                    Stadium.builder().name("문학").shortCode("ICN").team(teams.get("SK")).build(),  // SSG
                    Stadium.builder().name("수원").shortCode("SUW").team(teams.get("KT")).build(),    // KT
                    Stadium.builder().name("대전").shortCode("DJN").team(teams.get("HH")).build(), // 한화
                    Stadium.builder().name("대구").shortCode("DAE").team(teams.get("SS")).build(), // 삼성
                    Stadium.builder().name("사직").shortCode("BUS").team(teams.get("LT")).build(),       // 롯데
                    Stadium.builder().name("창원").shortCode("CHW").team(teams.get("NC")).build(),         // NC
                    Stadium.builder().name("광주").shortCode("GWJ").team(teams.get("HT")).build() // 기아
            ));
        }
    }
}