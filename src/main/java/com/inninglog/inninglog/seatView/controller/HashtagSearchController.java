package com.inninglog.inninglog.seatView.controller;

import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.dto.res.SeatViewImageResult;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
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
                    최대 **5개까지 태그 선택**이 가능하며, **선택한 모든 태그를 포함한 좌석만 조회**됩니다.  
                    해당 API는 **모아보기(사진만 제공)** 형태로 결과를 반환합니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해시태그 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "성공 응답 예시",
                                            value = """
                                            {
                                              "code": "SEATVIEW_LIST_FETCHED",
                                              "message": "시야 사진 조회 성공",
                                              "data": {
                                                "content": [
                                                  {
                                                    "seatViewId": 3,
                                                    "viewMediaUrl": "https://your-s3-bucket-url/image.jpg"
                                                  }
                                                ],
                                                "pageable": {
                                                  "pageNumber": 0,
                                                  "pageSize": 1,
                                                  "sort": {
                                                    "empty": false,
                                                    "sorted": true,
                                                    "unsorted": false
                                                  },
                                                  "offset": 0,
                                                  "paged": true,
                                                  "unpaged": false
                                                },
                                                "last": false,
                                                "totalElements": 3,
                                                "totalPages": 3,
                                                "first": true,
                                                "size": 1,
                                                "number": 0,
                                                "numberOfElements": 1,
                                                "empty": false
                                              }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "검색 결과 없음",
                                            value = """
                                            {
                                              "code": "SEAT_VIEW_EMPTY",
                                              "message": "해당 조건에 해당하는 시야 사진이 없습니다.",
                                              "data": {
                                                "content": [],
                                                "pageable": {
                                                  "pageNumber": 0,
                                                  "pageSize": 1,
                                                  "sort": {
                                                    "empty": false,
                                                    "sorted": true,
                                                    "unsorted": false
                                                  },
                                                  "offset": 0,
                                                  "unpaged": false,
                                                  "paged": true
                                                },
                                                "last": true,
                                                "totalElements": 0,
                                                "totalPages": 0,
                                                "first": true,
                                                "size": 1,
                                                "number": 0,
                                                "sort": {
                                                  "empty": false,
                                                  "sorted": true,
                                                  "unsorted": false
                                                },
                                                "numberOfElements": 0,
                                                "empty": true
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
                                              "message": "해시태그는 최소 1개, 최대 5개까지 선택할 수 있습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/gallery")
    public ResponseEntity<SuccessResponse<Page<SeatViewImageResult>>> searchSeatViewsGallery(
            @Parameter(description = "구장 단축코드", required = true, example = "JAM")
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = "해시태그 코드 목록 (최대 5개 선택 가능)",
                    required = true,
                    example = "VIEW_OPEN,SUN_STRONG,CHEERING_BEST",
                    schema = @Schema(type = "array")
            )
            @RequestParam List<String> hashtagCodes,

            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SeatViewImageResult> resultPage = hashtagSearchService.searchSeatViewsByHashtagsGallery(
                stadiumShortCode, hashtagCodes, pageable
        );

        SuccessCode code = (resultPage.isEmpty()) ? SuccessCode.SEATVIEW_EMPTY
                : SuccessCode.SEATVIEW_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, resultPage));
    }

    @Operation(
            summary = "해시태그 기반 좌석 검색 (게시물 형태)",
            description = """
                    선택한 감정 태그를 기준으로 좌석 시야 후기를 **게시물 형태**로 검색합니다.  
                    사진, 좌석 정보, 감정 태그 등 **상세한 정보를 모두 포함**하여 반환합니다.  
                    최대 **5개까지 태그 선택**이 가능하며, **모든 태그를 포함한 좌석만 조회**됩니다.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해시태그 상세 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            examples = {
                                    @ExampleObject(
                                            name = "페이징된 시야 사진 검색 결과",
                                            value = """
                                            {
                                              "code": "SEATVIEW_LIST_FETCHED",
                                              "message": "시야 사진 조회 성공",
                                              "data": {
                                                "content": [
                                                  {
                                                    "seatViewId": 3,
                                                    "viewMediaUrl": "https://your-s3-bucket-url/image.jpg",
                                                    "seatInfo": {
                                                      "zoneName": "블루석",
                                                      "zoneShortCode": "JAM_BLUE",
                                                      "section": "13",
                                                      "seatRow": "3",
                                                      "stadiumName": "잠실"
                                                    },
                                                    "emotionTags": [
                                                      {
                                                        "code": "CHEERING_MOSTLY_STANDING",
                                                        "label": "응원 - 일어날 사람은 일어남"
                                                      },
                                                      {
                                                        "code": "SUN_NONE",
                                                        "label": "햇빛 - 없음"
                                                      }
                                                    ]
                                                  }
                                                ],
                                                "pageable": {
                                                  "pageNumber": 0,
                                                  "pageSize": 1,
                                                  "sort": {
                                                    "empty": false,
                                                    "sorted": true,
                                                    "unsorted": false
                                                  },
                                                  "offset": 0,
                                                  "paged": true,
                                                  "unpaged": false
                                                },
                                                "last": false,
                                                "totalPages": 3,
                                                "totalElements": 3,
                                                "first": true,
                                                "size": 1,
                                                "number": 0,
                                                "numberOfElements": 1,
                                                "empty": false
                                              }
                                            }
                                            """
                                    ),
                                    @ExampleObject(
                                            name = "검색 결과 없음",
                                            value = """
                                            {
                                              "code": "SEAT_VIEW_EMPTY",
                                              "message": "해당 조건에 해당하는 시야 사진이 없습니다.",
                                              "data": {
                                                "content": [],
                                                "pageable": {
                                                  "pageNumber": 0,
                                                  "pageSize": 1,
                                                  "sort": {
                                                    "empty": false,
                                                    "sorted": true,
                                                    "unsorted": false
                                                  },
                                                  "offset": 0,
                                                  "unpaged": false,
                                                  "paged": true
                                                },
                                                "last": true,
                                                "totalElements": 0,
                                                "totalPages": 0,
                                                "first": true,
                                                "size": 1,
                                                "number": 0,
                                                "sort": {
                                                  "empty": false,
                                                  "sorted": true,
                                                  "unsorted": false
                                                },
                                                "numberOfElements": 0,
                                                "empty": true
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
                                              "message": "해시태그는 최소 1개, 최대 5개까지 선택할 수 있습니다."
                                            }
                                            """
                            )
                    )
            )
    })
    @GetMapping("/feed")
    public ResponseEntity<SuccessResponse<Page<SeatViewDetailResult>>> searchSeatViewsDetail(
            @Parameter(description = "구장 단축코드", required = true, example = "JAM")
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = "해시태그 코드 목록 (최대 5개 선택 가능)",
                    required = true,
                    example = "VIEW_OPEN,SUN_STRONG,CHEERING_BEST",
                    schema = @Schema(type = "array")
            )
            @RequestParam List<String> hashtagCodes,

            @PageableDefault(size = 10, sort = "createdAt", direction = Sort.Direction.DESC) Pageable pageable
    ) {
        Page<SeatViewDetailResult> response = hashtagSearchService.searchSeatViewsByHashtagsDetail(
                stadiumShortCode, hashtagCodes, pageable
        );

        SuccessCode code = (response.isEmpty()) ? SuccessCode.SEATVIEW_EMPTY
                : SuccessCode.SEATVIEW_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, response));
    }
}