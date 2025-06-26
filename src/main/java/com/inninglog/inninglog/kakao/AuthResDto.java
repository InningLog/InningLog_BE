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

    private String message;

    public static AuthResDto fromKakaoLoginRes(KakaoLoginResponse loginResponse) {
       return AuthResDto.builder()
                .nickname(loginResponse.getNickname())
                .isNewMember(loginResponse.isNewUser())
                .message(loginResponse.getMessage())
                .build();

    }

}
