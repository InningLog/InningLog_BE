package com.inninglog.inninglog.kakao;


import com.inninglog.inninglog.global.auth.AuthTempStorage;
import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessApiResponses;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "카카오 로그인", description = "카카오 관련 API")
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;
    private final AuthTempStorage authTempStorage;



    @Operation(
            summary = "카카오 로그인 콜백(프론트 신경 쓰지 않아도 됨)",
            description = "카카오 로그인 인가 코드를 통해 JWT 토큰과 사용자 정보를 반환합니다." +
                    "프론트에서 신경 쓰지 않아도 됨"
    )
    @ErrorApiResponses.Common
    @SuccessApiResponses.Login
    @GetMapping("/callback")
    public void callback(@RequestParam("code") String code, HttpServletResponse response) {
        try {
            KakaoLoginResponse kakaoRes = kakaoAuthService.loginWithKakao(code);
            String accessToken = kakaoRes.getHeaders().getFirst("Authorization");

            if (accessToken == null) {
                throw new RuntimeException("accessToken이 누락되었습니다.");
            }

            AuthResDto authResDto = AuthResDto.fromKakaoLoginRes(kakaoRes);

            Storage storage = Storage.from(authResDto.getNickname(), authResDto.isNewMember(), accessToken);

            String tempId = authTempStorage.save(storage, 180); // 3분 TTL

            String redirectUrl = String.format("https://inninglog.shop/#/bridge?id=%s", tempId);
            response.sendRedirect(redirectUrl);

            log.info(redirectUrl);
        } catch (Exception e) {
            log.error("❌ 카카오 로그인 에러", e);
            try {
                response.sendRedirect("https://inninglog.shop/login?error=1");
            } catch (IOException ioException) {
                log.error("❌ 에러 리다이렉트 실패", ioException);
            }
        }
    }
}
