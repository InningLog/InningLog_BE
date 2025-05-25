package com.inninglog.inninglog.kakao;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class KakaoLoginResponse {
    private String message;
    private String nickname;
}
