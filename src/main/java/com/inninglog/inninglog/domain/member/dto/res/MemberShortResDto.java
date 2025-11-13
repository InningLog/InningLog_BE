package com.inninglog.inninglog.domain.member.dto.res;

import com.inninglog.inninglog.domain.member.domain.Member;

public record MemberShortResDto(
        String nickName,
        String profile_url
) {
    public static MemberShortResDto from(Member member){
        return new MemberShortResDto(
                member.getNickname(),
                //개인 프로필로 변경되면 그걸로 바꾸기
                member.getKakao_profile_url()
        );
    }
}
