package com.inninglog.inninglog.seatView.dto.res;

import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.dto.req.SeatViewEmotionTagDto;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "좌석 상세 정보 및 감정 태그 응답 DTO")
public class SeatViewDetailResult {

    @Schema(description = "좌석 시야 정보의 고유 ID", example = "102")
    private Long seatViewId;

    @Schema(description = "좌석 시야 이미지 URL", example = "https://s3.ap-northeast-2.amazonaws.com/inninglog/seat/view123.jpg")
    private String viewMediaUrl;

    @Schema(description = "좌석 정보 (구역, 열 등)")
    private SeatInfo seatInfo;

    @Schema(description = "해당 좌석에 등록된 감정 태그 리스트")
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