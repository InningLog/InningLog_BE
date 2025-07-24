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

    public static SeatViewDetailResult from(SeatView seatView, String presignedUrl) {
        return SeatViewDetailResult.builder()
                .seatViewId(seatView.getId())
                .viewMediaUrl(presignedUrl)
                .build();
    }
}