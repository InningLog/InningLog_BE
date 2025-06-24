package com.inninglog.inninglog.member.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "응원 팀 설정 요청", example = "{\"teamShortCode\": \"OB\"}")
public class TypeRequestDto {

    @Schema(description = "응원 팀의 식별자 shortCode (예: DOOSAN, KIA, SSG 등)", example = "OB")
    private String teamShortCode;
}
