package com.inninglog.inninglog.kakao;


import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessApiResponses;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "카카오 로그인", description = "카카오 관련 API")
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;

    @Operation(
            summary = "카카오 로그인 콜백",
            description = "카카오 로그인 인가 코드를 통해 JWT 토큰과 사용자 정보를 반환합니다." +
                    "프론트에서 신경 쓰지 않아도 됨"
    )
    @ErrorApiResponses.Common
    @SuccessApiResponses.Login
    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            KakaoLoginResponse response = kakaoAuthService.loginWithKakao(code);
            AuthResDto authResDto = AuthResDto.fromKakaoLoginRes(response);

            return ResponseEntity
                    .status(SuccessCode.LOGIN_SUCCESS.getStatus())
                    .headers(response.getHeaders())
                    .body(SuccessResponse.success(SuccessCode.LOGIN_SUCCESS, authResDto));

        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            return new ResponseEntity<>("로그인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

