package com.inninglog.inninglog.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
@Schema(description = "로그인 성공 응답")
public class LoginSuccessSwaggerDto {

    @Schema(description = "응답 코드", example = "LOGIN_SUCCESS")
    private String code;

    @Schema(description = "응답 메시지", example = "로그인이 성공적으로 되었습니다.")
    private String message;

    @Schema(description = "응답 데이터")
    private AuthResDto data;

    public LoginSuccessSwaggerDto() {
        this.code = "LOGIN_SUCCESS";
        this.message = "로그인이 성공적으로 되었습니다.";
        this.data = new AuthResDto("닉네임", true);
    }
}
