package com.inninglog.inninglog.kakao;

import com.inninglog.inninglog.global.auth.JwtProvider;
import com.inninglog.inninglog.global.util.AmplitudeService;
import com.inninglog.inninglog.member.domain.Member;
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
        // 1. 카카오 토큰 발급
        String kakaoAccessToken = kakaoService.getAccessToken(code);

        // 2. 사용자 정보 조회
        KakaoUserInfoResponseDto userInfo = kakaoService.getUserInfo(kakaoAccessToken);

        // 3. 멤버 저장 또는 업데이트
        Member member = memberService.saveOrUpdateMember(userInfo);

        // 4. 이벤트 로깅
        amplitudeService.log(
                "user_login",
                "test-user-123",  // 추후 userId로 변경하면 좋음
                Map.of(
                        "login_method", "kakao",
                        "timestamp", String.valueOf(System.currentTimeMillis())
                )
        );

        // 5. JWT 발급
        String jwtAccessToken = jwtProvider.createToken(member.getId());
        String jwtRefreshToken = jwtProvider.createRefreshToken(member.getId());

        // 6. 응답 구성
        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + jwtAccessToken);
        headers.set("Refresh-Token", jwtRefreshToken);
        headers.set("kakaoId", member.getKakaoId().toString());

        return new KakaoLoginResponse("로그인 성공", member.getNickname(), headers);
    }
}