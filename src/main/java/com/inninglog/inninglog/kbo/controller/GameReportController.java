package com.inninglog.inninglog.kbo.controller;

import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.kbo.dto.gameReport.GameReportResDto;
import com.inninglog.inninglog.kbo.service.GameReportService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
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
@Tag(name = "직관 리포트", description = "직관 리포트 관련 API")
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
                    content = @Content(
                            schema = @Schema(implementation = GameReportResDto.class),
                            examples = @ExampleObject(
                                    name = "Success Example",
                                    summary = "직관 리포트 성공 응답 예시",
                                    value = """
                {
                  "code": "SUCCESS",
                  "message": "요청이 정상적으로 처리되었습니다.",
                  "data": {
                    "totalVisitedGames": 2,
                    "winGames": 2,
                    "loseGames": 0,
                    "drawGames": 0,
                    "myWeaningRate": 1000,
                    "teamWinRate": 0.415,
                    "topBatters": [
                      {
                        "playerId": 20,
                        "playerName": "케이브",
                        "playerType": "HITTER",
                        "totalHits": 1,
                        "totalAtBats": 3,
                        "totalEarned": 0,
                        "totalInning": 0,
                        "halPoongRi": 333
                      }
                    ],
                    "topPitchers": [
                      {
                        "playerId": 5,
                        "playerName": "홍건희",
                        "playerType": "PITCHER",
                        "totalHits": 0,
                        "totalAtBats": 0,
                        "totalEarned": 2,
                        "totalInning": 1111111111111111,
                        "halPoongRi": 0
                      }
                    ],
                    "bottomBatters": [
                      {
                        "playerId": 18,
                        "playerName": "정수빈",
                        "playerType": "HITTER",
                        "totalHits": 0,
                        "totalAtBats": 3,
                        "totalEarned": 0,
                        "totalInning": 0,
                        "halPoongRi": 0
                      }
                    ],
                    "bottomPitchers": [
                      {
                        "playerId": 4,
                        "playerName": "최민석",
                        "playerType": "PITCHER",
                        "totalHits": 0,
                        "totalAtBats": 0,
                        "totalEarned": 2,
                        "totalInning": 7,
                        "halPoongRi": 286
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
    @GetMapping("/main")
    public ResponseEntity<SuccessResponse<GameReportResDto>> mainPage(
            @AuthenticationPrincipal CustomUserDetails user){
        GameReportResDto gameReportResDto = gameReportService.generateReport(user.getMember().getId());

        return ResponseEntity.ok(SuccessResponse.success(SuccessCode.OK, gameReportResDto));
    }
}
