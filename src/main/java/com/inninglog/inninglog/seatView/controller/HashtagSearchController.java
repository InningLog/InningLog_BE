package com.inninglog.inninglog.seatView.controller;

import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.seatView.dto.req.HashtagSearchReq;
import com.inninglog.inninglog.seatView.dto.res.HashtagSearchRes;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.service.HashtagSearchService;
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

import java.util.List;

@RestController
@RequestMapping("/seatViews/hashtag")
@RequiredArgsConstructor
@Tag(name = "좌석 시야 검색", description = "구장 좌석 시야 후기 검색 API")
public class HashtagSearchController {

    private final HashtagSearchService hashtagSearchService;

    @Operation(
            summary = "해시태그 기반 좌석 검색 (모아보기)",
            description = """
        선택한 감정 태그를 기준으로 좌석 시야 후기를 검색합니다.  
        최대 **2개까지 태그 선택**이 가능하며, **AND / OR 조건** 중 선택하여 검색할 수 있습니다.  
        해당 API는 **모아보기(사진만 제공)** 형태로 결과를 반환합니다.
        
        ---
        ✅ **사용 가능한 태그 코드**
        - `VIEW_OPEN`     : **#시야_탁_트였어요**
        - `SUN_STRONG`    : **#햇빛이_강해서_모자는_필수**
        - `CHEERING_BEST`  : **#응원_분위기_최고**
        - `CHEER_STAGE_NEAR`: **#응원단상이_가까워요**
        - `GOOD_VALUE`    : **#가성비_좌석인듯**
        - `ROOF_SHELTER`   : **#지붕_있어서_비와도_안심**
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해시태그 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = HashtagSearchRes.class),
                            examples = {
                                    @ExampleObject(
                                            name = "OR 조건 검색 결과",
                                            summary = "해시태그 OR 검색 성공",
                                            description = "선택한 태그 중 하나라도 포함된 결과",
                                            value = """
                        {
                          "code": "SEATVIEW_LIST_FETCHED",
                          "message": "시야 사진 조회 성공",
                          "data": {
                            "searchSummary": "잠실 '시야_탁_트임, 햇빛이_강함' (태그 중 하나 이상 포함) 해시태그 검색 결과",
                            "seatViews": [
                              {
                                "seatViewId": 1,
                                "viewMediaUrl": "https://your-s3-bucket-url/image1.jpg"
                              },
                              {
                                "seatViewId": 3,
                                "viewMediaUrl": "https://your-s3-bucket-url/image2.jpg"
                              }
                            ],
                            "totalCount": 2,
                            "isGalleryView": true
                          }
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "AND 조건 검색 결과",
                                            summary = "해시태그 AND 검색 성공",
                                            description = "선택한 모든 태그를 포함한 결과",
                                            value = """
                        {
                          "code": "SEATVIEW_LIST_FETCHED",
                          "message": "시야 사진 조회 성공",
                          "data": {
                            "searchSummary": "잠실 '시야_탁_트임, 햇빛이_강함' (모든 태그 포함) 해시태그 검색 결과",
                            "seatViews": [
                              {
                                "seatViewId": 1,
                                "viewMediaUrl": "https://your-s3-bucket-url/image1.jpg"
                              }
                            ],
                            "totalCount": 1,
                            "isGalleryView": true
                          }
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "검색 결과 없음",
                                            summary = "해당 해시태그에 해당하는 시야 사진이 없음",
                                            description = "검색 조건에 맞는 시야 사진이 존재하지 않는 경우",
                                            value = """
                        {
                          "code": "SEAT_VIEW_EMPTY",
                          "message": "해당 조건에 해당하는 시야 사진이 없습니다.",
                          "data": {
                            "searchSummary": "잠실 '가성비_최고' 해시태그 검색 결과",
                            "seatViews": [],
                            "totalCount": 0,
                            "isGalleryView": true
                          }
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (해시태그 개수 초과 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "에러 응답 예시",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해시태그는 최소 1개, 최대 2개까지 선택할 수 있습니다."
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/gallery")
    public ResponseEntity<SuccessResponse<HashtagSearchRes>> searchSeatViewsGallery(
            @Parameter(
                    description = "구장 단축코드",
                    required = true,
                    example = "JAM"
            )
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = """
            해시태그 코드 목록 (최대 2개 선택 가능)
            """,
                    required = true,
                    example = "VIEW_OPEN,SUN_STRONG",
                    schema = @Schema(
                            type = "array",
                            allowableValues = {
                                    "VIEW_OPEN", "SUN_STRONG", "CHEERING_BEST",
                                    "CHEER_STAGE_NEAR", "GOOD_VALUE", "ROOF_SHELTER"
                            },
                            description = "해시태그 코드 배열 (최대 2개)"
                    )
            )
            @RequestParam List<String> hashtagCodes,

            @Parameter(
                    description = "AND 조건 여부 (true: 모든 태그 포함, false: 태그 중 하나 이상 포함)",
                    required = false,
                    example = "false"
            )
            @RequestParam(required = false, defaultValue = "false") Boolean isAndCondition
    ) {

        HashtagSearchRes response = hashtagSearchService.searchSeatViewsByHashtagsGallery(
                stadiumShortCode, hashtagCodes, isAndCondition
        );

        SuccessCode code = (response.getTotalCount() == 0) ? SuccessCode.SEATVIEW_EMPTY
                : SuccessCode.SEATVIEW_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, response));
    }




    @Operation(
            summary = "해시태그 기반 좌석 검색 (게시물 형태)",
            description = """
        선택한 감정 태그를 기준으로 좌석 시야 후기를 **게시물 형태**로 검색합니다.  
        사진, 좌석 정보, 감정 태그 등 **상세한 정보를 모두 포함**하여 반환합니다.
        
        ---
        ✅ **사용 가능한 태그 코드**
        - `VIEW_OPEN`     : **#시야_탁_트였어요**
        - `SUN_STRONG`    : **#햇빛이_강해서_모자는_필수**
        - `CHEERING_BEST`  : **#응원_분위기_최고**
        - `CHEER_STAGE_NEAR`: **#응원단상이_가까워요**
        - `GOOD_VALUE`    : **#가성비_좌석인듯**
        - `ROOF_SHELTER`   : **#지붕_있어서_비와도_안심**
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해시태그 상세 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SeatViewDetailResult.class),
                            examples = {
                                    @ExampleObject(
                                            name = "상세 검색 결과 있음",
                                            summary = "해시태그 상세 검색 성공",
                                            description = "검색 조건에 맞는 상세 정보를 포함한 좌석 시야 결과",
                                            value = """
                        {
                          "code": "SEATVIEW_LIST_FETCHED",
                          "message": "시야 사진 조회 성공",
                          "data": [
                            {
                              "seatViewId": 1,
                              "viewMediaUrl": "https://your-s3-bucket-url/image1.jpg",
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
                                  "label": "시야_탁_트임"
                                },
                                {
                                  "code": "CHEERING_BEST",
                                  "label": "응원_분위기_최고"
                                }
                              ]
                            },
                            {
                              "seatViewId": 3,
                              "viewMediaUrl": "https://your-s3-bucket-url/image2.jpg",
                              "seatInfo": {
                                "zoneName": "오렌지석",
                                "zoneShortCode": "JAM_ORANGE",
                                "section": "7구역",
                                "seatRow": "5열",
                                "stadiumName": "잠실"
                              },
                              "emotionTags": [
                                {
                                  "code": "VIEW_OPEN",
                                  "label": "시야_탁_트임"
                                },
                                {
                                  "code": "GOOD_VALUE",
                                  "label": "가성비_최고"
                                }
                              ]
                            }
                          ]
                        }
                        """
                                    ),
                                    @ExampleObject(
                                            name = "상세 검색 결과 없음",
                                            summary = "해당 해시태그에 해당하는 상세 정보가 없음",
                                            description = "검색 조건에 맞는 좌석 시야가 존재하지 않는 경우",
                                            value = """
                        {
                          "code": "SEAT_VIEW_EMPTY",
                          "message": "해당 조건에 해당하는 시야 사진이 없습니다.",
                          "data": []
                        }
                        """
                                    )
                            }
                    )
            ),
            @ApiResponse(
                    responseCode = "400",
                    description = "잘못된 요청 (해시태그 개수 초과 등)",
                    content = @Content(
                            mediaType = "application/json",
                            examples = @ExampleObject(
                                    name = "에러 응답 예시",
                                    value = """
                    {
                      "code": "BAD_REQUEST",
                      "message": "해시태그는 최소 1개, 최대 2개까지 선택할 수 있습니다."
                    }
                    """
                            )
                    )
            )
    })
    @GetMapping("/feed")
    public ResponseEntity<SuccessResponse<List<SeatViewDetailResult>>> searchSeatViewsDetail(
            @Parameter(
                    description = "구장 단축코드",
                    required = true,
                    example = "JAM"
            )
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = """
            해시태그 코드 목록 (최대 2개 선택 가능)
            """,
                    required = true,
                    example = "VIEW_OPEN,SUN_STRONG",
                    schema = @Schema(
                            type = "array",
                            allowableValues = {
                                    "VIEW_OPEN", "SUN_STRONG", "CHEERING_BEST",
                                    "CHEER_STAGE_NEAR", "GOOD_VALUE", "ROOF_SHELTER"
                            },
                            description = "해시태그 코드 배열 (최대 2개)"
                    )
            )
            @RequestParam List<String> hashtagCodes,

            @Parameter(
                    description = "AND 조건 여부 (true: 모든 태그 포함, false: 태그 중 하나 이상 포함)",
                    required = false,
                    example = "false"
            )
            @RequestParam(required = false, defaultValue = "false") Boolean isAndCondition
    ) {
        List<SeatViewDetailResult> response = hashtagSearchService.searchSeatViewsByHashtagsDetail(
                stadiumShortCode, hashtagCodes, isAndCondition
        );

        SuccessCode code = (response.isEmpty()) ? SuccessCode.SEATVIEW_EMPTY
                : SuccessCode.SEATVIEW_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, response));
    }
}