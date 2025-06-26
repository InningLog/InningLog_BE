package com.inninglog.inninglog.kakao;


import com.inninglog.inninglog.global.auth.JwtProvider;
import com.inninglog.inninglog.global.util.AmplitudeService;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class KakaoLoginController {

    private final KakaoAuthService kakaoAuthService;

    @GetMapping("/callback")
    public ResponseEntity<?> callback(@RequestParam("code") String code) {
        try {
            KakaoLoginResponse response = kakaoAuthService.loginWithKakao(code);

            AuthResDto authResDto = AuthResDto.fromKakaoLoginRes(response);

            return ResponseEntity.ok()
                    .headers(response.getHeaders())
                    .body(authResDto);
        } catch (Exception e) {
            log.error("Error during Kakao login process", e);
            return new ResponseEntity<>("로그인 중 오류가 발생했습니다.", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

}

