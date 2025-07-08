package com.inninglog.inninglog.seatView.controller;


import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.dto.req.SeatCreateReqDto;
import com.inninglog.inninglog.seatView.dto.res.SeatViewDetailResult;
import com.inninglog.inninglog.seatView.service.SeatViewService;
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
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seatViews")
@Tag(name = "좌석 시야", description = "좌석 시야글 관련 API")
public class SeatViewController {

    private final SeatViewService seatViewService;


    //좌석 시야 이미지 업로드
    @Operation(
            summary = "좌석 시야 이미지 업로드",
            description = "JWT 토큰에서 유저 정보를 추출하고, S3에 이미지를 업로드합니다. 이후 URL을 반환하며, 이후 JSON 생성 API에서 이 URL을 사용합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "이미지 업로드 성공",
                    content = @Content(schema = @Schema(implementation = String.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (파일 없음)",
                    content = @Content),
            @ApiResponse(responseCode = "500", description = "서버 에러 (S3 업로드 실패)",
                    content = @Content)
    })
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> uploadImage(

            @Parameter(description = "업로드할 이미지 파일")
            @RequestPart(value = "file", required = false) MultipartFile file
    ){
        if(file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 없습니다.");
        }
        try{
            String media_url = seatViewService.UploadImage(file);
            return ResponseEntity.ok().body(media_url);
        }catch (Exception e){
            return ResponseEntity.internalServerError().body("이미지 업로드 실패 " + e.getMessage());
        }
    }


    //좌석 시야 생성
    @Operation(
            summary = "좌석 시야 생성",
            description = "JWT 토큰에서 유저 정보를 추출하고, S3에 이미지 업로드 후 좌석 시야를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "좌석 시야 생성 성공",
                    content = @Content(schema = @Schema(implementation = SeatCreateReqDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청(JSON 형식 오류)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 또는 경기장 정보 없음",
                    content = @Content)
    })
    @PostMapping(value = "/contents")
    public ResponseEntity<?> createSeatView(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    description = """
                좌석 시야 작성 요청 JSON 예시입니다. 이 값을 복사해 'request' 필드에 붙여넣으세요.

                ```json
                {
                  "journalId": 1,
                  "stadiumShortCode": "JAM",
                  "zoneShortCode": "JAM_BLUE",
                  "section": "13구역",
                  "seatRow": "3열",
                  "emotionTagCodes": [
                    "VIEW_OPEN",
                    "SUN_STRONG"
                  ],
                  "media_url": "https://your-s3-bucket-url/image.jpg"
                }
                ```
                """,
                    required = true,
                    content = @Content(schema = @Schema(implementation = SeatCreateReqDto.class))
            )
            @RequestBody SeatCreateReqDto request)
    {
        SeatView seatView = seatViewService.createSeatView(user.getMember().getId(), request);
        return ResponseEntity.ok(seatView.getId());
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
                                                    "label": "시야_탁_트임"
                                                  },
                                                  {
                                                    "code": "SUN_STRONG",
                                                    "label": "햇빛이_강함"
                                                  }
                                                ]
                                              }
                                            }
                                            """
                            )
                    )
            ),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 seatViewId"),
            @ApiResponse(responseCode = "401", description = "인증되지 않은 사용자 접근")
    })
    @GetMapping("/{seatViewId}")
    public SuccessResponse<SeatViewDetailResult> getSeatView(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "조회할 SeatView의 ID", required = true)
            @PathVariable Long seatViewId
    ) {
        SeatViewDetailResult result = seatViewService.getSeatView(user.getMember().getId(), seatViewId);
        return SuccessResponse.success(SuccessCode.OK, result);
    }
}
