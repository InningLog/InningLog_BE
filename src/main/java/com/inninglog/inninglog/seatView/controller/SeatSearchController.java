package com.inninglog.inninglog.seatView.controller;

import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.seatView.dto.res.SeatSearchRes;
import com.inninglog.inninglog.seatView.service.SeatSearchService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/seatsView/normal")
@RequiredArgsConstructor
@Tag(name = "일반 좌석 검색", description = "구장 좌석 시야 후기 검색 API")
public class SeatSearchController {

    private final SeatSearchService seatSearchService;

    @Operation(
            summary = "좌석 검색",
            description = "구장, 존, 구역, 열 정보를 통해 좌석 시야 후기를 검색합니다. " +
                    "모든 조건은 선택사항이지만, 열 정보만으로는 검색할 수 없습니다 (최소 존 정보 필요)."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "좌석 시야 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = {
                                    @ExampleObject(
                                            name = "검색 결과 있음",
                                            summary = "시야 사진 조회 성공",
                                            description = "검색 조건에 맞는 시야 사진이 존재하는 경우",
                                            value = """
                        {
                          "code": "SEATVIEW_LIST_FETCHED",
                          "message": "시야 사진 조회 성공",
                          "data": {
                            "searchSummary": "잠실 블루석 검색 결과",
                            "seatViews": [
                              {
                                "seatViewId": 1,
                                "viewMediaUrl": "https://your-s3-bucket-url/image.jpg",
                                "seatInfo": {
                                  "zoneName": "블루석",
                                  "zoneShortCode": "JAM_BLUE",
                                  "section": "13구역",
                                  "seatRow": "3열",
                                  "stadiumName": "잠실"
                                },
                                "emotionTags": [
                                  {
                                    "code": "VIEW_OPEN",
                                    "label": "시야_탁_트였어요"
                                  },
                                  {
                                    "code": "SUN_STRONG",
                                    "label": "햇빛이_강해서_모자는_필수"
                                  }
                                ]
                              }
                            ],
                            "totalCount": 1
                          }
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "검색 결과 없음",
                                            summary = "해당 조건에 해당하는 시야 사진이 없음",
                                            description = "검색 조건에 맞는 시야 사진이 존재하지 않는 경우",
                                            value = """
                        {
                          "code": "SEAT_VIEW_EMPTY",
                          "message": "해당 조건에 해당하는 시야 사진이 없습니다.",
                          "data": {
                            "searchSummary": "잠실 블루석 15구역 검색 결과",
                            "seatViews": [],
                            "totalCount": 0
                          }
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (열 정보만 입력한 경우)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "에러 응답 예시",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "열 정보만으로는 검색할 수 없습니다. 최소 존 정보가 필요합니다."
                    }
                    """
                            )
                    )
            ),
            @ApiResponse(
                    responseCode = "500",
                    description = "서버 내부 오류"
            )
    })
    @GetMapping("/search")
    public ResponseEntity<SuccessResponse<SeatSearchRes>> searchSeats(
            @Parameter(
                    description = "구장 단축코드",
                    required = true,
                    example = "JAM",
                    schema = @Schema(type = "string", allowableValues = {
                            "JAM", "GOC", "ICN", "SUW", "DJN", "DAE", "BUS", "GWJ", "CHW"
                    })
            )
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = "존 단축코드 (선택사항)",
                    required = false,
                    example = "JAM_BLUE"
            )
            @RequestParam(required = false) String zoneShortCode,

            @Parameter(
                    description = "구역 정보 (선택사항)",
                    required = false,
                    example = "13"
            )
            @RequestParam(required = false) String section,

            @Parameter(
                    description = "열 정보 (선택사항, 단독 사용 불가 - 최소 존 정보 필요)",
                    required = false,
                    example = "3"
            )
            @RequestParam(required = false) String seatRow
    ) {
        SeatSearchRes response = seatSearchService.searchSeats(
                stadiumShortCode, zoneShortCode, section, seatRow
        );

        SuccessCode code = (response.getTotalCount() == 0) ? SuccessCode.SEATVIEW_EMPTY
                : SuccessCode.SEATVIEW_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, response));
    }
}