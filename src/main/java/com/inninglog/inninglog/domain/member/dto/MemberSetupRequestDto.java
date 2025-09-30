package com.inninglog.inninglog.domain.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class MemberSetupRequestDto {

    @Schema(description = "설정할 닉네임", example = "야구천재")
    private String nickname;

    @Schema(description = "응원팀 shortCode", example = "LG")
    private String teamShortCode;
}