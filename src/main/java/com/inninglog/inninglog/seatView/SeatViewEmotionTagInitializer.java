package com.inninglog.inninglog.seatView;

import com.inninglog.inninglog.seatView.domain.SeatViewEmotionTag;
import com.inninglog.inninglog.seatView.repository.SeatViewEmotionTagRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class SeatViewEmotionTagInitializer implements CommandLineRunner {

    private final SeatViewEmotionTagRepository tagRepository;

    @Override
    public void run(String... args) {
        if (tagRepository.count() == 0) {
            tagRepository.saveAll(List.of(
                    SeatViewEmotionTag.builder().code("VIBE_GOOD").label("분위기 짱").build(),
                    SeatViewEmotionTag.builder().code("CHEER_LOUD").label("응원 시끄러움").build(),
                    SeatViewEmotionTag.builder().code("QUIET").label("조용조용").build(),
                    SeatViewEmotionTag.builder().code("TOO_HOT").label("더움").build(),
                    SeatViewEmotionTag.builder().code("TOO_COLD").label("추움").build()
            ));
        }
    }
}
