package com.inninglog.inninglog.stadium;

import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
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
            Map<String, Team> teams = Stream.of("DOOSAN", "KIWOOM", "SSG", "KT", "HANWHA", "SAMSUNG", "LOTTE", "NC", "KIA")
                    .collect(Collectors.toMap(code -> code, code ->
                            teamRepository.findByShortCode(code)
                                    .orElseThrow(() -> new RuntimeException(code + " 팀이 존재하지 않습니다."))));

            stadiumRepository.saveAll(List.of(
                    Stadium.builder().name("잠실종합운동장 야구장").shortCode("JAM").team(teams.get("DOOSAN")).build(), //엘지랑 매핑
                    Stadium.builder().name("고척 스카이돔").shortCode("GOC").team(teams.get("KIWOOM")).build(),
                    Stadium.builder().name("인천 SSG 랜더스필드").shortCode("ICN").team(teams.get("SSG")).build(),
                    Stadium.builder().name("수원 KT 위즈파크").shortCode("SUW").team(teams.get("KT")).build(),
                    Stadium.builder().name("대전 한화생명 이글스파크").shortCode("DJN").team(teams.get("HANWHA")).build(),
                    Stadium.builder().name("대구 삼성 라이온즈 파크").shortCode("DAE").team(teams.get("SAMSUNG")).build(),
                    Stadium.builder().name("부산 사직야구장").shortCode("BUS").team(teams.get("LOTTE")).build(),
                    Stadium.builder().name("창원 NC파크").shortCode("CHW").team(teams.get("NC")).build(),
                    Stadium.builder().name("광주-기아 챔피언스 필드").shortCode("GWJ").team(teams.get("KIA")).build()
            ));
        }
    }
}