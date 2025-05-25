package com.inninglog.inninglog.kakao;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/login")
public class KakaoLoginPageController {

    @Value("${kakao.client_id}")
    private String client_id;

    @Value("${kakao.redirect_uri}")
    private String redirect_uri;

    @GetMapping("/page")
    public ResponseEntity<?> loginPage() {
        // 카카오 로그인 URL 생성
        String location = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id="
                + client_id + "&redirect_uri=" + redirect_uri;

        // JSON 형식으로 URL 반환
        return ResponseEntity.ok().body("{\"location\": \"" + location + "\"}");
    }
}
