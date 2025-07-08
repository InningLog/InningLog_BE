package com.inninglog.inninglog.seatView.dto.res;

import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SeatViewDetailResult {

    private Long seatViewId;
    private String viewMediaUrl;
    private SeatInfo seatInfo;
    private List<SeatViewEmotionTagDto> emotionTags;

    public static SeatViewDetailResult from(SeatView seatView, List<SeatViewEmotionTagDto> tags) {
        return SeatViewDetailResult.builder()
                .seatViewId(seatView.getId())
                .viewMediaUrl(seatView.getView_media_url())
                .seatInfo(SeatInfo.from(seatView))
                .emotionTags(tags)
                .build();
    }
}