package com.inninglog.inninglog.kakao;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpHeaders;

@Getter
@AllArgsConstructor
public class KakaoLoginResponse {
    private String nickname;
    private boolean isNewUser;
    private HttpHeaders headers;
}
