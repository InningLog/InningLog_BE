package com.inninglog.inninglog.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@Schema(description = "카카오 로그인 URL 응답")
public class KakaoLoginPageResponse {

    @Schema(description = "카카오 로그인 리다이렉트 URL",
            example = "https://kauth.kakao.com/oauth/authorize?response_type=code&client_id=abc123&redirect_uri=http://localhost:8080/callback")
    private String location;
}
