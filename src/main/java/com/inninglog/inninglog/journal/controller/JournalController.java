package com.inninglog.inninglog.journal.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessApiResponses;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.journal.dto.req.JourCreateReqDto;

import com.inninglog.inninglog.journal.dto.req.JourUpdateReqDto;
import com.inninglog.inninglog.journal.dto.res.*;

import com.inninglog.inninglog.journal.service.JournalService;
import com.inninglog.inninglog.kbo.dto.gameSchdule.GameSchResDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;

import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/journals")
@Tag(name = "직관 일지", description = "직관 일지 관련 API")
public class JournalController {

    private final JournalService journalService;

    //직관 일지 이미지 업로드
    @Operation(
            summary = "직관 일지 이미지 업로드",
            description = """
                JWT 토큰에서 유저 정보를 추출하고, S3에 이미지를 업로드합니다.  
                이후 반환된 URL을 JSON 생성 API에 사용합니다.
                """
    )
    @ErrorApiResponses.Common
    @ErrorApiResponses.S3Failed
    @SuccessApiResponses.FileUpload
    @PostMapping(value = "/upload-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<SuccessResponse<String>> uploadImage(
            @Parameter(description = "업로드할 이미지 파일", required = true)
            @RequestPart("file") MultipartFile file
    ) {
        String url = journalService.uploadImage(file);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.S3_UPLOAD_SUCCESS, url));
    }


    //직관 일지 콘텐츠 업로드
    @Operation(
            summary = "직관 일지 콘텐츠 업로드",
            description = """
        직관 일지 본문 데이터를 업로드하는 API입니다. 
        
        사용자는 사전에 이미지 파일을 S3 업로드 API를 통해 업로드하고,  
        응답받은 media URL을 포함한 JSON 데이터를 본 API에 전달합니다.
        
        이 API는 전달받은 정보로 새로운 Journal 객체를 생성합니다.

        ✅ 필수 필드:
        - `gameId`: 경기 게임 Id
        - `media_url`: 이미지 S3 URL
        - `ourScore`, `theirScore`: 점수 정보
        - `opponentTeamShortCode`, `stadiumShortCode`
        - `date`, `emotion`
        """
    )
    @ErrorApiResponses.Common
    @ErrorApiResponses.Game
    @SuccessApiResponses.JournalCreate
    @PostMapping("/contents")
    public ResponseEntity<SuccessResponse<JourCreateResDto>> createContents(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @io.swagger.v3.oas.annotations.parameters.RequestBody(
                    required = true,
                    content = @Content(schema = @Schema(implementation = JourCreateReqDto.class))
            )
            @RequestBody JourCreateReqDto request)
    {
        JourCreateResDto resDto = journalService.createJournal(user.getMember().getId(), request);

        return ResponseEntity.ok(
                SuccessResponse.success(SuccessCode.JOURNAL_CREATED, resDto)
        );
    }




    //본인 직관일지 목록 조회(캘린더)
    @Operation(
            summary = "본인 직관 일지 목록 조회 - 캘린더",
            description = """
                JWT 토큰에서 유저 정보를 추출하여 본인의 직관 일지를 조회합니다.

                ✅ 선택적으로 `resultScore` 파라미터를 통해 경기 결과에 따른 필터링이 가능합니다.

                📌 필터링 예시:
                - `/journals/calendar?resultScore=승`
                - `/journals/calendar?resultScore=패`
                - `/journals/calendar?resultScore=무승부`

                🔁 필터링 가능한 값:
                - 승 (WIN)
                - 패 (LOSE)
                - 무승부 (DRAW)
                """
    )
    @ErrorApiResponses.Common
    @SuccessApiResponses.JournalList
    @GetMapping("/calendar")
    public ResponseEntity<SuccessResponse<List<JournalCalListResDto>>> getCalendarJournals(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) ResultScore resultScore
    ) {
        List<JournalCalListResDto> result = journalService.getJournalsByMemberCal(user.getMember().getId(), resultScore);

        SuccessCode code = result.isEmpty()
                ? SuccessCode.JOURNAL_EMPTY
                : SuccessCode.JOURNAL_LIST_FETCHED;

        return ResponseEntity.ok(SuccessResponse.success(code, result));
    }





    //본인 직관일지 목록 조회(모아보기)
    @Operation(
            summary = "본인 직관 일지 목록 조회 - 모아보기",
            description = """
        로그인한 유저의 직관 일지를 목록 형식으로 조회합니다.

        📌 *무한 스크롤 방식 지원*  
        🔍 *`resultScore` 파라미터를 통해 경기 결과(WIN, LOSE, DRAW)로 필터링 가능*  
        🧭 *`page`, `size` 파라미터로 페이지네이션 처리 (기본: 1페이지당 10개)*  

        ✅ 예시 요청:
        - 전체 조회: `/journals/summary?page=0&size=10`
        - 승리 경기만: `/journals/summary?page=1&size=10&resultScore=WIN`
        """
    )
    @ErrorApiResponses.Common
    @SuccessApiResponses.JournalList
    @GetMapping("/summary")
    public ResponseEntity<SuccessResponse<Page<JournalSumListResDto>>> getMyJournalsSum(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "페이징 정보 (page: 0부터 시작, size: 페이지당 아이템 수)", example = "0")
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,

            @Parameter(description = "경기 결과 필터 (WIN, LOSE, DRAW)", example = "WIN")
            @RequestParam(required = false) ResultScore resultScore
    ) {
        Page<JournalSumListResDto> result = journalService.getJournalsByMemberSum(user.getMember().getId(), pageable, resultScore);

        SuccessCode code = result.isEmpty() ? SuccessCode.JOURNAL_EMPTY : SuccessCode.JOURNAL_LIST_FETCHED;
        return ResponseEntity.ok(SuccessResponse.success(code, result));
    }


    @Operation(
            summary = "직관 일지 콘텐츠 사전 정보 조회",
            description = """
    해당 경기 ID(gameId)를 기반으로, 현재 로그인한 사용자의 응원 팀과 상대 팀 정보를 조회합니다.
      
    - 이 API는 직관 일지 작성을 시작하기 전, 필요한 기본 정보를 제공합니다.  
    - 반환되는 데이터는 사용자의 응원 팀, 상대 팀, 경기장 정보, 경기 일시 등을 포함합니다.  
    - 유저의 응원 팀은 미리 설정되어 있어야 하며, gameId는 유효한 경기여야 합니다.
    """
    )
    @ErrorApiResponses.Common
    @ErrorApiResponses.Game
    @SuccessApiResponses.JournalInfo
    @GetMapping("/contents")
    public ResponseEntity<SuccessResponse<JourGameResDto>> getGameInfo(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "경기 Id (gameId)", required = true)
            @RequestParam String gameId
    ){
        JourGameResDto resDto = journalService.infoJournal(user.getMember().getId(), gameId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resDto));
    }


    //특정 날짜 경기 일정 조회 - 유저의 응원팀 기준
    @Operation(
            summary = "유저 응원팀의 특정 날짜 경기 일정 조회",
            description = """
            로그인한 유저의 **응원팀 기준으로**, 특정 날짜의 경기 일정을 조회합니다.  
            
            반환된 `gameId`는 이후 **직관 일지 콘텐츠 업로드 API (`/journals/contents`)**에 사용됩니다.

            🗓️ 요청 날짜는 `YYYY-MM-DD` 형식으로 전달해야 합니다.

            ✅ 예시:
            `/journals/schedule?gameDate=2025-07-01`
        """
    )
    @ErrorApiResponses.Common
    @ErrorApiResponses.Game
    @SuccessApiResponses.GameSchedule
    @GetMapping("/schedule")
    public ResponseEntity<SuccessResponse<GameSchResDto>> getSchedule(
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "경기 일정 날짜 (예: 2025-07-01)", required = true)
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate gameDate
    ){
        GameSchResDto resDto = journalService.getSingleGameSch(user.getMember().getId(), gameDate);

        if (resDto == null) {
            return ResponseEntity.ok(SuccessResponse.success(SuccessCode.NO_SCHEDULE_ON_DATE, null));
        }
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resDto));
    }

    @Operation(
            summary = "특정 직관일지 상세 조회",
            description = "journalId는 직관일지 목록 API(/summary, /schedule)를 통해 확인된 값을 전달해야 합니다. seatViewId는 시야 정보가 연결된 경우에만 포함됩니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "직관일지 상세 조회 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JourDetailResDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "시야 정보 미포함",
                                                    summary = "seatView가 연결되지 않은 경우",
                                                    value = """
                        {
                          "code": "SUCCESS",
                          "message": "요청이 정상적으로 처리되었습니다.",
                          "data": {
                            "jourDetail": {
                              "journalId": 4,
                              "gameDate": "2025-06-30T13:06:40.457",
                              "supportTeamSC": "OB",
                              "opponentTeamSC": "OB",
                              "stadiumSC": "JAM",
                              "emotion": "감동",
                              "media_url": "",
                              "review_text": ""
                            },
                            "seatViewId": null
                          }
                        }
                        """
                                            ),
                                            @ExampleObject(
                                                    name = "seatView 연결됨",
                                                    summary = "시야 정보가 연결된 경우",
                                                    value = """
                        {
                          "code": "SUCCESS",
                          "message": "요청이 정상적으로 처리되었습니다.",
                          "data": {
                            "jourDetail": {
                              "journalId": 3,
                              "gameDate": "2025-07-08T12:11:39.038",
                              "supportTeamSC": "OB",
                              "opponentTeamSC": "OB",
                              "stadiumSC": "JAM",
                              "emotion": "감동",
                              "media_url": "https://s3.amazonaws.com/.../image.jpg",
                              "review_text": "오늘 정말 재미있었다!"
                            },
                            "seatViewId": 3
                          }
                        }
                        """
                                            )
                                    }
                            )
                    )
            }
    )
    @GetMapping("/detail/{journalId}")
    public ResponseEntity<SuccessResponse<JourUpdateResDto>> getDetailJournal(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "직관 일지 ID. 목록 API에서 선택한 항목의 ID를 전달", required = true)
            @PathVariable Long journalId
    ) {
        JourUpdateResDto resDto = journalService.getDetailJournal(user.getMember().getId(), journalId);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, resDto));

    }



    @Operation(
            summary = "직관 일지 수정",
            description = """
        기존에 작성된 직관 일지 내용을 수정합니다. 본인만 수정할 수 있으며, 
        수정 시 감정 태그, 리뷰, 점수, 이미지 링크 등을 포함할 수 있습니다.
        seatView는 별도 API로 연결되며 본 API에서 수정되지 않습니다.
    """,
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "직관일지 수정 성공",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = JourUpdateResDto.class),
                                    examples = {
                                            @ExampleObject(
                                                    name = "수정 완료 예시",
                                                    summary = "수정 완료된 직관일지와 seatViewId 반환",
                                                    value = """
                        {
                          "code": "SUCCESS",
                          "message": "요청이 정상적으로 처리되었습니다.",
                          "data": {
                            "jourDetail": {
                              "journalId": 3,
                              "gameDate": "2025-07-08T12:11:39.038",
                              "supportTeamSC": "OB",
                              "opponentTeamSC": "OB",
                              "stadiumSC": "JAM",
                              "emotion": "감동",
                              "media_url": "https://s3.amazonaws.com/.../image.jpg",
                              "review_text": "후기를 수정했어요!"
                            },
                            "seatViewId": 3
                          }
                        }
                        """
                                            )
                                    }
                            )
                    )
            }
    )
    @PatchMapping("/update/{journalId}")
    @ErrorApiResponses.Common
    public ResponseEntity<SuccessResponse<JourUpdateResDto>> updateJournal(
            @AuthenticationPrincipal CustomUserDetails user,
            @PathVariable Long journalId,
            @RequestBody JourUpdateReqDto dto
    ) {
        JourUpdateResDto updatedJournal = journalService.updateJournal(user.getMember().getId(), journalId, dto);
        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, updatedJournal));
    }
}

