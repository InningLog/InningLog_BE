package com.inninglog.inninglog.seatView.controller;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.journal.domain.Journal;
import com.inninglog.inninglog.journal.dto.JourCreateReqDto;
import com.inninglog.inninglog.journal.dto.JourCreateResDto;
import com.inninglog.inninglog.seatView.domain.SeatView;
import com.inninglog.inninglog.seatView.dto.SeatCreateReqDto;
import com.inninglog.inninglog.seatView.dto.SeatCreateResDto;
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
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import org.springframework.web.multipart.MultipartFile;


import java.awt.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/seatView")
@Tag(name = "SeatView", description = "좌석 시야 관련 API")
public class SeatViewController {

    private final SeatViewService seatViewService;

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
    @PostMapping(value = "/create", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> createSeatView(
            @Parameter(hidden = true)
            @AuthenticationPrincipal CustomUserDetails user,

            @Parameter(description = "업로드할 이미지 파일")
            @RequestPart(value = "file", required = false) MultipartFile file,

            @Parameter(
                    description = """
                            좌석 시야 작성 요청 JSON 예시입니다. 이 값을 복사해 'request' 필드에 붙여넣으세요.
                            
                            ```json
                            {
                              "journalId": 1,
                              "seatInfo": "13구역 3열",
                              "stadiumShortCode": "JAM",
                              "emotionTagCodes": [
                                "VIBE_GOOD",
                                "QUIET"
                              ]
                            }
                            ```
                            """,
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = SeatCreateReqDto.class)
                    )
            )
            @RequestPart("request") String requestJson
    ) {
        try {
            // 받은 JSON 문자열 로그 출력 (디버깅용)
            System.out.println("Received JSON: " + requestJson);

            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.registerModule(new com.fasterxml.jackson.datatype.jsr310.JavaTimeModule());
            objectMapper.disable(com.fasterxml.jackson.databind.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

            SeatCreateReqDto request = objectMapper.readValue(requestJson, SeatCreateReqDto.class);

            // 서비스 로직 호출
            SeatView seatView = seatViewService.createSeatView(user.getMember().getId(), request, file);
            return ResponseEntity.status(201).body(new SeatCreateResDto(seatView.getId(), seatView.getJournal().getId()));

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
