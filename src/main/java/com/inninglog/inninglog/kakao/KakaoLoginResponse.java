package com.inninglog.inninglog.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
@AllArgsConstructor
public class KakaoLoginResponse {
    private String message;
    private String nickname;
    private HttpHeaders headers;
}
