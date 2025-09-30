package com.inninglog.inninglog.domain.seatView.dto.req;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema(description = "좌석 후기 등록 요청 DTO")
public class SeatCreateReqDto {

    @Schema(description = "매핑되는 직관 일지의 ID", example = "12")
    private Long journalId;

    @Schema(description = "경기장 숏코드 (ex. JAMS, DAE)", example = "JAM")
    private String stadiumShortCode;

    @Schema(description = "존 숏코드 (ex. JAM_BLUE )", example = "JAM_BLUE")
    private String zoneShortCode;

    @Schema(description = "좌석의 구역 정보", example = "101")
    private String section;

    @Schema(description = "좌석의 열(Row) 정보", example = "3")
    private String seatRow;

    @Schema(description = "감정 태그 코드 리스트", example = "[    \"CHEERING_MOSTLY_STANDING\", \"SUN_NONE\"]")
    private List<String> emotionTagCodes;

    @Schema(description = "업로드할 이미지 파일명 (확장자 포함)", example = "photo123.jpeg")
    private String fileName;
}