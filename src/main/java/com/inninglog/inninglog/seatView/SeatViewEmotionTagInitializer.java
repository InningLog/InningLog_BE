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
                    SeatViewEmotionTag.builder().code("VIEW_OPEN").label("시야_탁_트임").build(),
                    SeatViewEmotionTag.builder().code("SUN_STRONG").label("햇빛이_강함").build(),
                    SeatViewEmotionTag.builder().code("CHEERING_BEST").label("응원_분위기_최고").build(),
                    SeatViewEmotionTag.builder().code("CHEER_STAGE_NEAR").label("응원단상이_가까움").build(),
                    SeatViewEmotionTag.builder().code("GOOD_VALUE").label("가성비_최고").build(),
                    SeatViewEmotionTag.builder().code("ROOF_SHELTER").label("지붕_있음").build()
            ));
        }
    }
}