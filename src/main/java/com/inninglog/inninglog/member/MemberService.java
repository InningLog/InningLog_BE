package com.inninglog.inninglog.member;

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
                    newMember.setNickname(kakaoUserInfo.getKakaoAccount().getProfile().getNickName());
                    newMember.setProfileImageUrl(kakaoUserInfo.getKakaoAccount().getProfile().getProfileImageUrl());
                    return memberRepository.save(newMember); // join 대신 save
                });
    }
}
