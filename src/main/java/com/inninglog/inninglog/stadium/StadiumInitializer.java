package com.inninglog.inninglog.stadium;

import com.inninglog.inninglog.stadium.domain.Stadium;
import com.inninglog.inninglog.stadium.repository.StadiumRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Component
@RequiredArgsConstructor
public class StadiumInitializer implements CommandLineRunner {

    private final StadiumRepository stadiumRepository;
    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) {
        Map<String, Team> teams = Stream.of("DOOSAN", "KIWOOM", "SSG", "KT", "HANWHA", "SAMSUNG", "LOTTE", "NC", "KIA")
                .collect(Collectors.toMap(code -> code, code -> teamRepository.findByShortCode(code)
                        .orElseThrow(() -> new RuntimeException(code + " 팀이 존재하지 않습니다."))));

            stadiumRepository.saveAll(List.of(
                Stadium.builder().name("잠실종합운동장 야구장").short_code("JAM").team(teams.get("DOOSAN")).build(),
                Stadium.builder().name("고척 스카이돔").short_code("GOC").team(teams.get("KIWOOM")).build(),
                Stadium.builder().name("인천 SSG 랜더스필드").short_code("ICN").team(teams.get("SSG")).build(),
                Stadium.builder().name("수원 KT 위즈파크").short_code("SUW").team(teams.get("KT")).build(),
                Stadium.builder().name("대전 한화생명 이글스파크").short_code("DJN").team(teams.get("HANWHA")).build(),
                Stadium.builder().name("대구 삼성 라이온즈 파크").short_code("DAE").team(teams.get("SAMSUNG")).build(),
                Stadium.builder().name("부산 사직야구장").short_code("BUS").team(teams.get("LOTTE")).build(),
                Stadium.builder().name("창원 NC파크").short_code("CHW").team(teams.get("NC")).build(),
                Stadium.builder().name("광주-기아 챔피언스 필드").short_code("GWJ").team(teams.get("KIA")).build()
            ));
        }
    }
