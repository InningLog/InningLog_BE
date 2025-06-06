package com.inninglog.inninglog.team;

import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(1)
public class TeamInitializer implements ApplicationRunner {

    private final TeamRepository teamRepository;

    @Override
    public void run(ApplicationArguments args) {
        if (teamRepository.count() == 0) {
            teamRepository.saveAll(List.of(
                    Team.builder().name("기아 타이거즈").shortCode("KIA").build(),
                    Team.builder().name("삼성 라이온즈").shortCode("SAMSUNG").build(),
                    Team.builder().name("LG 트윈스").shortCode("LG").build(),
                    Team.builder().name("두산 베어스").shortCode("DOOSAN").build(),
                    Team.builder().name("KT 위즈").shortCode("KT").build(),
                    Team.builder().name("SSG 랜더스").shortCode("SSG").build(),
                    Team.builder().name("롯데 자이언츠").shortCode("LOTTE").build(),
                    Team.builder().name("한화 이글스").shortCode("HANWHA").build(),
                    Team.builder().name("NC 다이노스").shortCode("NC").build(),
                    Team.builder().name("키움 히어로즈").shortCode("KIWOOM").build()
            ));
        }
    }
}