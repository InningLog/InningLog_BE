package com.inninglog.inninglog.domain.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
@AllArgsConstructor
public class KakaoLoginResponse {
    private String nickname;
    private boolean isNewUser;
    private String accessToken;
    private String refreshToken;}
