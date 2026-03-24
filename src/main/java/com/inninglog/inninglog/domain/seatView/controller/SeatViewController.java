package com.inninglog.inninglog.domain.seatView.controller;


import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.domain.seatView.dto.req.SeatCreateReqDto;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatCreateResDto;
import com.inninglog.inninglog.domain.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.domain.seatView.service.SeatViewService;
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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seatViews")
@Tag(name = "좌석시야", description = "좌석 시야 후기 관련 API")
public class SeatViewController {

    private final SeatViewService seatViewService;


    //좌석 시야 생성
    @Operation(
            summary = "좌석 시야 생성",
            description = """
    JWT 토큰에서 유저 정보를 추출하고, S3에 이미지 업로드 후 좌석 시야를 생성합니다.
            좌석 시야 작성 요청 JSON 예시입니다. S3에 이미지를 업로드한 후,
        업로드된 파일명을 'fileName' 필드에 포함하여 요청해야 합니다.

        ✅ 필수 입력 필드:
        - `journalId`: 연결된 직관 일지 ID
        - `stadiumShortCode`, `section`, `seatRow`: 좌석 정보
        - `emotionTagCodes`: 감정 태그 코드 배열 (문자열 리스트)
        - `fileNames`: 업로드한 이미지 파일명 리스트 (최대 5장, 확장자 포함)

        📌 Presigned URL을 통해 업로드된 파일명만 저장하며, 실제 S3 경로는 서버에서 조립됩니다.

    """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "201",
                    description = "좌석 시야 생성 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SuccessResponse.class),
                            examples = @ExampleObject(
                                    name = "좌석 시야 생성 성공 응답",
                                    summary = "정상 생성 시 응답 구조",
                                    description = "좌석 시야 생성이 완료되면 시야 ID와 연동된 일지 ID가 반환됩니다.",
                                    value = """
                {
                  "code": "SUCCESS",
                  "message": "요청이 정상적으로 처리되었습니다.",
                  "data": {
                    "seatViewId": 12,
                    "journalId": 7
                  }
                }
                """
                            )
                    )
            )
    })
    @ErrorApiResponses.Common
    @PostMapping(value = "/contents")
    public ResponseEntity<SuccessResponse<?>> createSeatView(
            @AuthenticationPrincipal CustomUserDetails user,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = SeatCreateReqDto.class))
            )
            @RequestBody SeatCreateReqDto request)
    {
        SeatCreateResDto resDto = seatViewService.createSeatView(user.getMember().getId(), request);
        return ResponseEntity.ok(
                SuccessResponse.success(SuccessCode.OK, resDto));
    }

    @Operation(
            summary = "특정 좌석 시야 조회",
            description = "seatViewId에 해당하는 좌석 시야 데이터를 상세 조회합니다."
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "조회 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SeatViewDetailResult.class),
                            examples = @ExampleObject(
                                    name = "정상 조회 예시",
                                    summary = "좌석 시야 상세 응답",
                                    value = """
                                            {
                                              "code": "SUCCESS",
                                              "message": "요청이 정상적으로 처리되었습니다.",
                                              "data": {
                                                "seatViewId": 3,
                                                "viewMediaUrls": ["https://your-s3-bucket-url/image1.jpg", "https://your-s3-bucket-url/image2.jpg"],
                                                "seatInfo": {
                                                  "section": "13",
                                                  "seatRow": "3",
                                                  "stadiumName": "잠실"
                                                },
                                                "emotionTags": [
                                                  {
                                                    "code": "VIEW_OBSTRUCT_NET",
                                                    "label": "시야 방해 - 그물"
                                                  },
                                                  {
                                                    "code": "SUN_STRONG",
                                                    "label": "햇빛 - 강함"
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            )
    })
    @ErrorApiResponses.Common
    @GetMapping("/{seatViewId}")
    public SuccessResponse<SeatViewDetailResult> getSeatView(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "조회할 SeatView의 ID", required = true)
            @PathVariable Long seatViewId
    ) {
        SeatViewDetailResult result = seatViewService.getSeatView(user.getMember().getId(),seatViewId);
        return SuccessResponse.success(SuccessCode.OK, result);
    }
}
