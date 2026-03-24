package com.inninglog.inninglog.domain.seatView.dto.res;

import com.inninglog.inninglog.domain.seatView.domain.SeatView;
import com.inninglog.inninglog.domain.seatView.dto.req.SeatViewEmotionTagDto;
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

    @Schema(description = "좌석 시야 이미지 URL 목록")
    private List<String> viewMediaUrls;

    @Schema(description = "좌석 기본 정보")
    private SeatInfo seatInfo;

    @Schema(description = "감정 태그 목록")
    private List<SeatViewEmotionTagDto> emotionTags;

    public static SeatViewDetailResult from(SeatView seatView, List<String> imageUrls,
                                            String section, String seatRow,
                                            String stadiumName,
                                            List<SeatViewEmotionTagDto> emotionTags) {
        return SeatViewDetailResult.builder()
                .seatViewId(seatView.getId())
                .viewMediaUrls(imageUrls)
                .seatInfo(new SeatInfo(section, seatRow, stadiumName))
                .emotionTags(emotionTags)
                .build();
    }

    @Getter
    @AllArgsConstructor
    public static class SeatInfo {
        private String section;
        private String seatRow;
        private String stadiumName;
    }
}