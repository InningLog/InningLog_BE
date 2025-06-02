package com.inninglog.inninglog.member.dto;

import com.inninglog.inninglog.member.domain.MemberType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

@Data
@Schema(description = "회원 타입 및 응원 팀 설정 요청", example = "{\"memberType\": \"NEWBIE\", \"teamShortCode\": \"KIA\"}")
public class TypeRequestDto {
    @Schema(description = "회원 타입 (NEWBIE 또는 VETERAN)", example = "NEWBIE")
    private MemberType memberType; // String → MemberType enum 자동 매핑

    @Schema(description = "응원 팀의 식별자 shortCode (예: DOOSAN, KIA, SSG 등)", example = "KIA")
    private String teamShortCode;
}
