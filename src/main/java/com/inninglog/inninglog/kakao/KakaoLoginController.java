package com.inninglog.inninglog.kakao;


import com.inninglog.inninglog.global.auth.JwtProvider;
import com.inninglog.inninglog.global.response.CustomApiResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import com.inninglog.inninglog.global.util.AmplitudeService;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.service.MemberService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(
            summary = "카카오 로그인 콜백",
            description = "카카오 로그인 인가 코드를 통해 JWT 토큰과 사용자 정보를 반환합니다."
    )
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "로그인 성공",
                    content = @Content(
                            mediaType = "application/json",
                            schema = @Schema(implementation = LoginSuccessSwaggerDto.class)
                    )
            ),
            @ApiResponse(responseCode = "500", description = "서버 내부 오류",
                    content = @Content(schema = @Schema(implementation = ErrorResponse.class))
            )
    })
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            KakaoLoginResponse response = kakaoAuthService.loginWithKakao(code);
            AuthResDto authResDto = AuthResDto.fromKakaoLoginRes(response);

            return ResponseEntity
                    .status(SuccessCode.LOGIN_SUCCESS.getStatus())
                    .headers(response.getHeaders())
                    .body(CustomApiResponse.success(SuccessCode.LOGIN_SUCCESS, authResDto));

        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            return new ResponseEntity<>("로그인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

