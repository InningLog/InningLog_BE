package com.inninglog.inninglog.member;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.kakao.KakaoUserInfoResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;

    @Transactional
    public Member saveOrUpdateMember(KakaoUserInfoResponseDto kakaoUserInfo) {
        Long kakaoId = kakaoUserInfo.getId();
        return memberRepository.findByKakaoId(kakaoId)
                .orElseGet(() -> {
                    Member newMember = new Member();
                    newMember.setKakaoId(kakaoId);
                    newMember.setKakao_nickname(kakaoUserInfo.getKakaoAccount().getProfile().getNickName());
                    newMember.setKakao_profile_url(kakaoUserInfo.getKakaoAccount().getProfile().getProfileImageUrl());
                    return memberRepository.save(newMember); // join 대신 save
                });
    }

    @Transactional
    public void updateNickname(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        member.setNickname(nickname);
    }

    @Transactional
    public void updateMemberType(Long memberId, String memberType) {}
}
