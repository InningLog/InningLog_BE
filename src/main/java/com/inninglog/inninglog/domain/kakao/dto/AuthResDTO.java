package com.inninglog.inninglog.domain.kakao.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResDTO {
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "신규 가입 여부")
    private boolean isNewMember;

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;


    public static AuthResDTO fromKakaoLoginRes(KakaoLoginResDTO loginResponse) {
       return AuthResDTO.builder()
                .nickname(loginResponse.getNickname())
                .isNewMember(loginResponse.isNewUser())
               .accessToken(loginResponse.getAccessToken())
               .refreshToken(loginResponse.getRefreshToken())
                .build();

    }

}
