package com.inninglog.inninglog.seatView.dto.res;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

// 모아보기용 - 사진만
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "좌석 사진 조회 결과 (모아보기용)")
public class SeatViewImageResult {

    @Schema(description = "좌석 시야 ID", example = "101")
    private Long seatViewId;

    @Schema(description = "시야 이미지 URL", example = "https://inninglog.s3.ap-northeast-2.amazonaws.com/seat/12345.jpg")
    private String viewMediaUrl;
}