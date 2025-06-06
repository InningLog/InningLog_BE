package com.inninglog.inninglog.journal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.domain.ResultScore;
import com.inninglog.inninglog.journal.dto.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.JourCreateResDto;
import com.inninglog.inninglog.journal.dto.JournalCalListResDto;
import com.inninglog.inninglog.journal.dto.JournalSumListResDto;
import com.inninglog.inninglog.journal.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Pageable;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/journals")
@Tag(name = "Journal", description = "직관 일지 관련 API")
public class JournalController {

    private final JournalService journalService;

    //직관 일지 생성
    @Operation(
            summary = "직관 일지 생성",
            description = "JWT 토큰에서 유저 정보를 추출하고, S3에 이미지 업로드 후 직관 일지를 생성합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "직관 일지 생성 성공",
                    content = @Content(schema = @Schema(implementation = JourCreateResDto.class))),
            @ApiResponse(responseCode = "400", description = "잘못된 요청 (JSON 형식 오류)",
                    content = @Content),
            @ApiResponse(responseCode = "404", description = "회원 또는 팀/경기장 정보 없음",
                    content = @Content)
    })
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createJournal(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "업로드할 이미지 파일 (선택사항)")
            @RequestPart(value = "file", required = false) MultipartFile file,

            @Parameter(
                    description = """
        일지 생성 요청 JSON 예시입니다. 이 값을 복사해 'request' 필드에 붙여넣으세요.

        ```json
        {
          "ourScore": 1,
          "stadiumShortCode": "JAM",
          "date": "2025-06-03T18:30:00",
          "opponentTeamShortCode": "KIA",
          "review_text": "오늘 경기는 완벽했어!",
          "theirScore": 0,
          "emotion": "기쁨",
          "is_public": true
        }
        ```
        """,
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JourCreateReqDto.class))
            )
            @RequestPart("request") String requestJson
    ) {
        try {
            // 받은 JSON 문자열 로그 출력 (디버깅용)
            System.out.println("Received JSON: " + requestJson);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            JourCreateReqDto request = objectMapper.readValue(requestJson, JourCreateReqDto.class);

            // 서비스 로직 호출
            Journal journal = journalService.createJournal(user.getMember().getId(), request, file);
            return ResponseEntity.status(201).body(new JourCreateResDto(journal.getId()));

        } catch (JsonProcessingException e) {
            // JSON 파싱 에러 처리
            return ResponseEntity.badRequest()
                    .body("Invalid JSON format: " + e.getMessage());
        } catch (Exception e) {
            // 기타 예외 처리
            return ResponseEntity.internalServerError()
                    .body("Server error: " + e.getMessage());
        }


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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직관 일지 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JournalCalListResDto.class)))),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                    content = @Content)
    })
    @GetMapping("/calendar")
    public ResponseEntity<?> getMyJournalsCal(
            @AuthenticationPrincipal CustomUserDetails user,
            @RequestParam(required = false) ResultScore resultScore
    ) {
        List<JournalCalListResDto> result = journalService.getJournalsByMemberCal(user.getMember().getId(), resultScore);
        return ResponseEntity.ok(result);
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
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "직관 일지 목록 조회 성공",
                    content = @Content(array = @ArraySchema(schema = @Schema(implementation = JournalCalListResDto.class)))),
            @ApiResponse(responseCode = "404", description = "회원 정보 없음",
                    content = @Content)
    })
    @GetMapping("/summary")
    public ResponseEntity<?> getMyJournalsSum(
            @AuthenticationPrincipal CustomUserDetails user,
            @Parameter(description = "페이징 정보 (page: 0부터 시작, size: 페이지당 아이템 수)", example = "0")
            @PageableDefault(size = 10, sort = "date", direction = Sort.Direction.DESC) Pageable pageable,

            @Parameter(description = "경기 결과 필터 (WIN, LOSE, DRAW)", example = "WIN")
            @RequestParam(required = false) ResultScore resultScore
    ) {
        Page<JournalSumListResDto> result = journalService.getJournalsByMemberSum(user.getMember().getId(), pageable, resultScore);
        return ResponseEntity.ok(result);
    }
}

