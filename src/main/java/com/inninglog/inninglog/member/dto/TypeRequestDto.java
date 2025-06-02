package com.inninglog.inninglog.member.dto;

import com.inninglog.inninglog.member.domain.MemberType;
import lombok.Data;

@Data
public class TypeRequestDto {
    private MemberType memberType; // String → MemberType enum 자동 매핑
    private Long team;
}
