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
                            schema = @Schema(implementation = HashtagSearchRes.class)
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
    public ResponseEntity<SuccessResponse<HashtagSearchRes>> searchSeatViewsGallery(
            @Parameter(description = "구장 단축코드", required = true, example = "JAM")
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = "해시태그 코드 목록 (최대 5개 선택 가능)",
                    required = true,
                    example = "VIEW_OPEN,SUN_STRONG,CHEERING_BEST",
                    schema = @Schema(type = "array")
            )
            @RequestParam List<String> hashtagCodes
    ) {
        HashtagSearchRes response = hashtagSearchService.searchSeatViewsByHashtagsGallery(
                stadiumShortCode, hashtagCodes
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
        최대 **5개까지 태그 선택**이 가능하며, **모든 태그를 포함한 좌석만 조회**됩니다.
        """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "해시태그 상세 검색 완료",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SeatViewDetailResult.class)
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
    public ResponseEntity<SuccessResponse<List<SeatViewDetailResult>>> searchSeatViewsDetail(
            @Parameter(description = "구장 단축코드", required = true, example = "JAM")
            @RequestParam String stadiumShortCode,

            @Parameter(
                    description = "해시태그 코드 목록 (최대 5개 선택 가능)",
                    required = true,
                    example = "VIEW_OPEN,SUN_STRONG,CHEERING_BEST",
                    schema = @Schema(type = "array")
            )
            @RequestParam List<String> hashtagCodes
    ) {
        List<SeatViewDetailResult> response = hashtagSearchService.searchSeatViewsByHashtagsDetail(
                stadiumShortCode, hashtagCodes
        );

        SuccessCode code = (response.isEmpty()) ? SuccessCode.SEATVIEW_EMPTY
                : SuccessCode.SEATVIEW_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, response));
    }
}
