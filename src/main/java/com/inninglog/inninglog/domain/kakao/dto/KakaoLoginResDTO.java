package com.inninglog.inninglog.domain.kakao.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class KakaoLoginResDTO {
    private String nickname;
    private boolean isNewUser;
    private String accessToken;
    private String refreshToken;}
