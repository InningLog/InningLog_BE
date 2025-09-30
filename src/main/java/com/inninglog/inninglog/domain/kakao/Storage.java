package com.inninglog.inninglog.domain.kakao;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Storage {
    @Schema(description = "닉네임")
    private String nickname;

    @Schema(description = "신규 가입 여부")
    private boolean isNewMember;

    private String accessToken;



    public static Storage from(String nickname, boolean isNewMember, String accessToken) {
        return Storage.builder()
                .nickname(nickname)
                .isNewMember(isNewMember)
                .accessToken(accessToken)
                .build();
    }

}
