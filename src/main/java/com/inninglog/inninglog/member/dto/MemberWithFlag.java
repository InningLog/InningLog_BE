package com.inninglog.inninglog.member.dto;

import com.inninglog.inninglog.member.domain.Member;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public class MemberWithFlag {
    private final Member member;
    private final boolean isNew;
}