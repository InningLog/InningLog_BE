package com.inninglog.inninglog.domain.kakao;

import com.inninglog.inninglog.global.auth.service.JwtProvider;
import com.inninglog.inninglog.domain.member.domain.Member;
import com.inninglog.inninglog.domain.member.dto.MemberWithFlag;
import com.inninglog.inninglog.domain.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;

    public KakaoLoginResponse loginWithKakao(String code) {
        String kakaoAccessToken = kakaoService.getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        MemberWithFlag result = memberService.saveOrUpdateMember(userInfo);
        Member member = result.getMember();
        boolean isNewUser = result.isNew();

        String jwtAccessToken = jwtProvider.createToken(member.getId());
        String jwtRefreshToken = jwtProvider.createRefreshToken(member.getId());


        return new KakaoLoginResponse(member.getNickname(), isNewUser, jwtAccessToken, jwtRefreshToken);
    }
}