package com.inninglog.inninglog.kakao;

import com.inninglog.inninglog.global.auth.JwtProvider;
import com.inninglog.inninglog.global.util.AmplitudeService;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.dto.MemberWithFlag;
import com.inninglog.inninglog.member.service.MemberService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class KakaoAuthService {

    private final KakaoService kakaoService;
    private final MemberService memberService;
    private final JwtProvider jwtProvider;
    private final AmplitudeService amplitudeService;

    public KakaoLoginResponse loginWithKakao(String code) {
        String kakaoAccessToken = kakaoService.getAccessToken(code);
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        MemberWithFlag result = memberService.saveOrUpdateMember(userInfo);
        Member member = result.getMember();
        boolean isNewUser = result.isNew();

        amplitudeService.log(
                isNewUser ? "user_signup" : "user_login",
                String.valueOf(member.getId()),
                Map.of(
                        "login_method", "kakao",
                        "timestamp", String.valueOf(System.currentTimeMillis())
                )
        );

        String jwtAccessToken = jwtProvider.createToken(member.getId());
        String jwtRefreshToken = jwtProvider.createRefreshToken(member.getId());

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtAccessToken);
        headers.set("Refresh-Token", jwtRefreshToken);
        headers.set("kakaoId", member.getKakaoId().toString());

        return new KakaoLoginResponse(member.getNickname(), isNewUser, headers);
    }
}