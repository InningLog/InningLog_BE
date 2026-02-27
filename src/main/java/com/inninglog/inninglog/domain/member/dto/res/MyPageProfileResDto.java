package com.inninglog.inninglog.domain.member.dto.res;

import com.inninglog.inninglog.domain.member.domain.Member;

public record MyPageProfileResDto(
        String nickname,
        String profileUrl,
        String teamShortCode,
        long totalGameCount,
        long winCount,
        double winRate
) {
    public static MyPageProfileResDto from(Member member, long totalCount, long winCount) {
        double rate = totalCount == 0 ? 0.0
                : Math.round((double) winCount / totalCount * 1000) / 10.0;
        return new MyPageProfileResDto(
                member.getNickname(),
                member.getProfile_url(),
                member.getTeam() != null ? member.getTeam().getShortCode() : null,
                totalCount,
                winCount,
                rate
        );
    }
}
