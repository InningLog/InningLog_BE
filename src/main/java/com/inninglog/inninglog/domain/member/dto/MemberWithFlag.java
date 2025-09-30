package com.inninglog.inninglog.domain.member.dto;

import com.inninglog.inninglog.domain.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberWithFlag {
    private final Member member;
    private final boolean isNew;
}