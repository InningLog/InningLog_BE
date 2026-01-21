package com.inninglog.inninglog.domain.home.controller;

import com.inninglog.inninglog.domain.home.usecase.HomeUsecase;
import com.inninglog.inninglog.global.auth.CustomUserDetails;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.domain.home.dto.HomeResDTO;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/home")
@Tag(name = "홈", description = "홈 관련 API")
public class HomeController {

    private final HomeUsecase homeUsecase;

    @Operation(
            summary = "홈 화면 정보 조회",
            description = "유저의 직관 승률과 응원팀 이번 달 경기 일정을 반환합니다.",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "요청 성공 시 홈 데이터 반환",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = HomeResDTO.class),
                                    examples = @ExampleObject(
                                            name = "홈 응답 예시",
                                            summary = "직관 승률과 경기 일정",
                                            value = """
                    {
                      "code": "SUCCESS",
                      "message": "요청이 정상적으로 처리되었습니다.",
                      "data": {
                        "nickName": "구혜승",
                        "supportTeamSC": "OB",
                        "myWeaningRate": 1000,
                        "myTeamSchedule": [
                          {
                            "myTeam": "OB",
                            "opponentTeam": "SS",
                            "stadium": "JAM",
                            "gameDateTime": "2025-07-01 18:30"
                          },
                          {
                            "myTeam": "OB",
                            "opponentTeam": "KT",
                            "stadium": "JAM",
                            "gameDateTime": "2025-07-04 18:30"
                          }
                        ]
                      }
                    }
                    """
                                    )
                            )
                    )
            }
    )
    @ErrorApiResponses.Common
    @GetMapping("/view")
    public ResponseEntity<SuccessResponse<HomeResDTO>> viewHome(
            @AuthenticationPrincipal CustomUserDetails user) {

        HomeResDTO resDto = homeUsecase.homeView(user.getMember().getId());

        return ResponseEntity.ok(
                SuccessResponse.success(SuccessCode.OK, resDto)
        );
    }

}
