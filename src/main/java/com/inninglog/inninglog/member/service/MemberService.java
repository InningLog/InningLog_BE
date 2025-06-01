package com.inninglog.inninglog.member.service;

import com.inninglog.inninglog.global.exception.CustomException;
import com.inninglog.inninglog.global.exception.ErrorCode;
import com.inninglog.inninglog.kakao.KakaoUserInfoResponseDto;
import com.inninglog.inninglog.member.domain.Member;
import com.inninglog.inninglog.member.domain.MemberType;
import com.inninglog.inninglog.member.repository.MemberRepository;
import com.inninglog.inninglog.team.domain.Team;
import com.inninglog.inninglog.team.repository.TeamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class MemberService {
    private final MemberRepository memberRepository;
    private final TeamRepository teamRepository;

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

    //닉네임 업데이트
    @Transactional
    public void updateNickname(Long memberId, String nickname) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        member.setNickname(nickname);
    }


    //유저 타입 & 응원 팀 설정
    @Transactional
    public void updateMemberType(Long memberId, String userType, Long teamId) {

        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        if(member.getTeam() != null || member.getMemberType() != null) {
            throw new CustomException(ErrorCode.ALREADY_SET);
        }

        //유저 타입 설정
        member.setMemberType(MemberType.valueOf(userType));

        //응원 팀 설정
        Team team = teamRepository.findById(teamId)
                .orElseThrow(() -> new CustomException(ErrorCode.TEAM_NOT_FOUND));

        member.setTeam(team);
    }
}
