package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.kbo.dto.gameReport.GameReportResDto;
import com.inninglog.inninglog.kbo.service.GameReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/report")
@RequiredArgsConstructor
@Tag(name = "GameReport", description = "직관 리포트 관련 API")
public class GameReportController {

    private final GameReportService gameReportService;

    //직관 리포트 페이지
    //직관 승률 + 선수 순위
    @Operation(
            summary = "직관 리포트 정보 가져오기",
            description = """
        로그인한 유저의 직관 기록을 바탕으로 리포트를 제공합니다.
        
        제공 정보:
        1. 직관 경기 수
        2. 응원팀 직관 승리/무승부/패배 수
        3. 유저의 직관 승률 (할푼리)
        4. 유저의 응원팀 승률
        5. 응원팀 상하위 타자 각각 1명
        6. 응원팀 상하위 투수 각각 1명
        """
    )
    @ApiResponses(value = {
            @ApiResponse(
                    responseCode = "200",
                    description = "직관 승률 조회 성공",
                    content = @Content(schema = @Schema(implementation = GameReportResDto.class))
            ),
            @ApiResponse(
                    responseCode = "404",
                    description = "회원 정보 없음",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/main")
    public ResponseEntity<?> mainPage(
            @AuthenticationPrincipal CustomUserDetails user){
        GameReportResDto gameReportResDto = gameReportService.generateReport(user.getMember().getId());

        return ResponseEntity.ok(gameReportResDto);
    }
}
