package com.inninglog.inninglog.team;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class TeamInitializer implements CommandLineRunner {

    private final TeamRepository teamRepository;

    @Override
    public void run(String... args) {
        if (teamRepository.count() == 0) { // 중복 삽입 방지
            teamRepository.saveAll(List.of(
                    Team.builder().name("기아 타이거즈").short_code("KIA").build(),
                    Team.builder().name("삼성 라이온즈").short_code("SAMSUNG").build(),
                    Team.builder().name("LG 트윈스").short_code("LG").build(),
                    Team.builder().name("두산 베어스").short_code("DOOSAN").build(),
                    Team.builder().name("KT 위즈").short_code("KT").build(),
                    Team.builder().name("SSG 랜더스").short_code("SSG").build(),
                    Team.builder().name("롯데 자이언츠").short_code("LOTTE").build(),
                    Team.builder().name("한화 이글스").short_code("HANWHA").build(),
                    Team.builder().name("NC 다이노스").short_code("NC").build(),
                    Team.builder().name("키움 히어로즈").short_code("KIWOOM").build()
            ));
        }
    }
}
