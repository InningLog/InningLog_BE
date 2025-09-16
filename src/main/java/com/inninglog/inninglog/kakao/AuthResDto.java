package com.inninglog.inninglog.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class AuthResDto {
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "신규 가입 여부")
    private boolean isNewMember;

    @Schema(description = "액세스 토큰")
    private String accessToken;

    @Schema(description = "리프레쉬 토큰")
    private String refreshToken;


    public static AuthResDto fromKakaoLoginRes(KakaoLoginResponse loginResponse) {
       return AuthResDto.builder()
                .nickname(loginResponse.getNickname())
                .isNewMember(loginResponse.isNewUser())
               .accessToken(loginResponse.getAccessToken())
               .refreshToken(loginResponse.getRefreshToken())
                .build();

    }

}
