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

    @Operation(
            summary = "직관 일지 이미지 업로드",
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
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "업로드할 이미지 파일 (선택사항)")
            @RequestPart(value = "file", required = false) MultipartFile file
    ) {
        if (file == null || file.isEmpty()) {
            return ResponseEntity.badRequest().body("파일이 없습니다.");
        }

        try {
           Journal journal = journalService.uploadImage(user.getMember().getId(), file);
            return ResponseEntity.ok(journal.getId());
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("이미지 업로드 실패: " + e.getMessage());
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

