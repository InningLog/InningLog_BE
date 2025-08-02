package com.inninglog.inninglog.kakao;


import com.inninglog.inninglog.global.exception.ErrorApiResponses;
import com.inninglog.inninglog.global.response.SuccessApiResponses;
import com.inninglog.inninglog.global.response.SuccessResponse;
import com.inninglog.inninglog.global.response.SuccessCode;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
@Tag(name = "카카오 로그인", description = "카카오 관련 API")
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;

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
            String refreshToken = kakaoRes.getHeaders().getFirst("Refresh-Token");

            if (accessToken == null || refreshToken == null) {
                throw new RuntimeException("토큰이 누락되었습니다.");
            }

            // ✅ 토큰 문자열 정리 (Bearer 제거)
            String tokenValue = accessToken.replace("Bearer ", "");

            boolean isNewUser = kakaoRes.isNewUser();
            log.info("isNewUser: {}", isNewUser);

            // ✅ URL 인코딩
            String encodedToken = URLEncoder.encode(tokenValue, StandardCharsets.UTF_8);

            // ✅ 쿼리 파라미터로 토큰과 isNewUser 전달
            String redirectUrl;
            if (isNewUser) {
                redirectUrl = "https://inninglog.shop/?isNewUser=true&jwt=" + encodedToken + "#/onboarding6";
            } else {
                redirectUrl = "https://inninglog.shop/?isNewUser=false&jwt=" + encodedToken + "#/home";
            }

            log.info("redirectUrl: {}", redirectUrl);
            response.sendRedirect(redirectUrl);

        } catch (Exception e) {
            log.error("카카오 로그인 중 에러 발생", e);
            try {
                response.sendRedirect("https://inninglog.shop/login?error=1");
            } catch (IOException ioException) {
                log.error("리다이렉트 에러", ioException);
            }
        }
    }

//    @GetMapping("/callback")
//    public void callback(@RequestParam("code") String code, HttpServletResponse response) {
//        try {
//            KakaoLoginResponse kakaoRes = kakaoAuthService.loginWithKakao(code);
//
//            String accessToken = kakaoRes.getHeaders().getFirst("Authorization");
//            String refreshToken = kakaoRes.getHeaders().getFirst("Refresh-Token");
//
//            if (accessToken == null || refreshToken == null) {
//                throw new RuntimeException("토큰이 누락되었습니다.");
//            }
//
//            //쿠키로 설정
//            Cookie accessCookie = new Cookie("ACCESS_TOKEN", accessToken.replace("Bearer ", ""));
//            accessCookie.setHttpOnly(true);
//            accessCookie.setSecure(true);
//            accessCookie.setPath("/");
//            accessCookie.setMaxAge(60 * 60);
//
//            Cookie refreshCookie = new Cookie("REFRESH_TOKEN", refreshToken);
//            refreshCookie.setHttpOnly(true);
//            refreshCookie.setSecure(true);
//            refreshCookie.setPath("/");
//            refreshCookie.setMaxAge(60 * 60 * 24 * 7);
//
//            response.addCookie(accessCookie);
//            response.addCookie(refreshCookie);
//
//            boolean isNewUser = kakaoRes.isNewUser();
//
//            log.info(String.valueOf(isNewUser));
//
//            String redirectUrl;
//
//            if (isNewUser) {
//                redirectUrl = "https://inninglog.shop/?isNewUser=true#/onboarding6";
//                log.info("redirectUrl: {}", redirectUrl);
//            } else {
//                redirectUrl = "https://inninglog.shop/?isNewUser=false#/home";
//                log.info("redirectUrl: {}", redirectUrl);
//            }
//
//            response.sendRedirect(redirectUrl);
//
//        } catch (Exception e) {
//            log.error("카카오 로그인 중 에러 발생", e);
//            try {
//                response.sendRedirect("https://inninglog.shop/login?error=1");
//            } catch (IOException ioException) {
//                log.error("리다이렉트 에러", ioException);
//            }
//        }
//    }

}

