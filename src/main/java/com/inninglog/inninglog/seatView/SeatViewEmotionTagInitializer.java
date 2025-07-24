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
                    // 응원
                    SeatViewEmotionTag.builder().code("CHEERING_STANDING").label("응원 - 일어남").build(),
                    SeatViewEmotionTag.builder().code("CHEERING_MOSTLY_STANDING").label("응원 - 일어날 사람은 일어남").build(),
                    SeatViewEmotionTag.builder().code("CHEERING_SEATED").label("응원 - 앉아서").build(),

                    // 햇빛
                    SeatViewEmotionTag.builder().code("SUN_STRONG").label("햇빛 - 강함").build(),
                    SeatViewEmotionTag.builder().code("SUN_MOVES_TO_SHADE").label("햇빛 - 있다가 그늘짐").build(),
                    SeatViewEmotionTag.builder().code("SUN_NONE").label("햇빛 - 없음").build(),

                    // 지붕
                    SeatViewEmotionTag.builder().code("ROOF_EXISTS").label("지붕 - 있음").build(),
                    SeatViewEmotionTag.builder().code("ROOF_NONE").label("지붕 - 없음").build(),

                    // 시야 방해
                    SeatViewEmotionTag.builder().code("VIEW_OBSTRUCT_NET").label("시야 방해 - 그물").build(),
                    SeatViewEmotionTag.builder().code("VIEW_OBSTRUCT_ACRYLIC").label("시야 방해 - 아크릴 가림막").build(),
                    SeatViewEmotionTag.builder().code("VIEW_NO_OBSTRUCTION").label("시야 방해 - 없음").build(),

                    // 좌석 공간
                    SeatViewEmotionTag.builder().code("SEAT_SPACE_VERY_WIDE").label("좌석 공간 - 아주 넓음").build(),
                    SeatViewEmotionTag.builder().code("SEAT_SPACE_WIDE").label("좌석 공간 - 넓음").build(),
                    SeatViewEmotionTag.builder().code("SEAT_SPACE_NORMAL").label("좌석 공간 - 보통").build(),
                    SeatViewEmotionTag.builder().code("SEAT_SPACE_NARROW").label("좌석 공간 - 좁음").build()
            ));
        }
    }
}