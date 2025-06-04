package com.inninglog.inninglog.journal.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.dto.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.JourCreateResDto;
import com.inninglog.inninglog.journal.service.JournalService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

            @Parameter(description = "일지 생성 요청 데이터",
                    content = @Content(mediaType = "application/json",
                            schema = @Schema(implementation = JourCreateReqDto.class)),
                    example = """
                    {
                      "ourScore": 0,
                      "stadiumShortCode": "JAM",
                      "date": "2025-06-03",
                      "opponentTeamShortCode": "KIA",
                      "review_text": "오늘 경기 정말 재밌었다!",
                      "resultScore": "승",
                      "theirScore": 0,
                      "emotion": "기쁨",
                      "is_public": true
                    }
                    """)
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
}